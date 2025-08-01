package com.pro.project01.v2.domain.type.repository;

import com.pro.project01.v2.domain.type.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, Long> {
}
