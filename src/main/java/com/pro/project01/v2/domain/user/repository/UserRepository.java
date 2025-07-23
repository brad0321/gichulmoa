package com.pro.project01.v2.domain.user.repository;

import com.pro.project01.v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndUsername(String email, String username);

    Optional<User> findByUsername(String username);
}
