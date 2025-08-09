package com.pro.project01.v2.domain.user.service;

import com.pro.project01.v2.domain.user.dto.UserLoginRequest;
import com.pro.project01.v2.domain.user.dto.UserRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.dto.UserUpdateRequest;

public interface UserService {
    UserResponse register(UserRequest request);
    UserResponse login(UserLoginRequest request);
    UserResponse getById(Long id);
    UserResponse updateMyInfo(Long id, UserUpdateRequest request);

    String findUsernameByEmail(String email);
    String resetPasswordToTemp(String email);
}
