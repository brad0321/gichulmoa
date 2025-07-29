package com.pro.project01.v2.domain.user.service;

import com.pro.project01.v2.domain.user.dto.UserRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRequest request); // ✅ 반환형을 UserResponse로 변경
    UserResponse login(String username, String password);
}
