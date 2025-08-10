package com.pro.project01.v2.domain.user.repository;

import com.pro.project01.v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // (선택) 자기 자신 제외 중복 체크가 필요할 때 사용
    boolean existsByEmailAndIdNot(String email, Long id);
}
