package com.joel.inventoryviewer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class InventoryApiClient {
    private static final String API_URL = "http://localhost:8080/api";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public static CompletableFuture<JsonArray> getBases() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/bases"))
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonArray.class));
    }

    public static CompletableFuture<JsonObject> createBase(String name, String description) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/bases"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<JsonObject> createStorage(int baseId, String type, int x, int y, int z, String biome) {
        JsonObject json = new JsonObject();
        json.addProperty("baseId", baseId);
        json.addProperty("typeName", type);
        json.addProperty("posX", x);
        json.addProperty("posY", y);
        json.addProperty("posZ", z);
        json.addProperty("biomeName", biome);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/storages"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<JsonObject> updateBase(int baseId, String name, String description) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/bases/" + baseId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<Void> deleteBase(int baseId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/bases/" + baseId))
                .DELETE()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> null);
    }

    public static CompletableFuture<Void> unregisterStorage(int baseId, int x, int y, int z) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/storages/base/" + baseId + "/coords?x=" + x + "&y=" + y + "&z=" + z))
                .DELETE()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
                    }
                    return null;
                });
    }

    public static CompletableFuture<JsonArray> searchPlayer(String name) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/players/search?query=" + name))
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonArray.class));
    }

    public static CompletableFuture<JsonObject> createPlayer(String name, String uuid) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("uuid", uuid);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/players"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<JsonObject> addMemberToBase(int baseId, int playerId, String role) {
        JsonObject json = new JsonObject();
        json.addProperty("baseId", baseId);
        json.addProperty("playerId", playerId);
        json.addProperty("role", role);
        json.addProperty("accepted", true);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/base-members"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<JsonObject> addTagToBase(int baseId, String tag, String scope, int color) {
        JsonObject json = new JsonObject();
        json.addProperty("baseId", baseId);
        json.addProperty("tag", tag);
        json.addProperty("scope", scope);
        json.addProperty("color", color);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/base-tags"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<Void> deleteTag(int tagId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/base-tags/" + tagId))
                .DELETE()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> null);
    }

    public static CompletableFuture<JsonObject> updateTag(int tagId, int baseId, String tag, String scope, int color) {
        JsonObject json = new JsonObject();
        json.addProperty("baseId", baseId);
        json.addProperty("tag", tag);
        json.addProperty("scope", scope);
        json.addProperty("color", color);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/base-tags/" + tagId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<JsonArray> getCatalog(String category) {
        String url = API_URL + "/catalog/" + category;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(res -> GSON.fromJson(res.body(), JsonArray.class));
    }

    public static CompletableFuture<JsonObject> getPlayerByUuid(String uuid) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/players/uuid/" + uuid))
                .GET()
                .build();
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 404) return null;
                    return GSON.fromJson(response.body(), JsonObject.class);
                });
    }

    public static CompletableFuture<JsonObject> setActiveBase(int playerId, int baseId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/players/" + playerId + "/active-base/" + baseId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonObject.class));
    }

    public static CompletableFuture<JsonArray> getStoragesByBaseId(int baseId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/storages/base/" + baseId))
                .GET()
                .build();
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> GSON.fromJson(response.body(), JsonArray.class));
    }

    public static CompletableFuture<JsonObject> syncStorage(int baseId, int x, int y, int z, String type, String biome, JsonArray items) {
        JsonObject json = new JsonObject();
        json.addProperty("baseId", baseId);
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        json.addProperty("typeName", type);
        json.addProperty("biomeName", biome);
        json.add("items", items);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/storages/sync"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(json)))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
                    }
                    if (response.body() == null || response.body().trim().isEmpty()) {
                        return new JsonObject();
                    }
                    return GSON.fromJson(response.body(), JsonObject.class);
                });
    }
}
