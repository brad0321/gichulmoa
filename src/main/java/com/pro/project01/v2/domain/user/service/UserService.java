package com.pro.project01.v2.domain.user.service;

import com.pro.project01.v2.domain.user.dto.UserCreateRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.dto.UserUpdatePasswordRequest;

public interface UserService {

    Long register(UserCreateRequest request);

    UserResponse findById(Long id);

    void delete(Long id);

    void updatePassword(Long id, UserUpdatePasswordRequest request);

    UserResponse findByEmail(String email);

    UserResponse findByEmailAndName(String email, String name);

    UserResponse login(String username, String password);
}
