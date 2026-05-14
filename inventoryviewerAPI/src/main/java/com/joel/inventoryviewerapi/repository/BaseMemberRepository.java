package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.BaseMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseMemberRepository extends JpaRepository<BaseMember, Integer> {}
