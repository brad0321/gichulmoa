package com.pro.project01.v2.domain.user.service;

import com.pro.project01.v2.domain.user.dto.UserCreateRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.dto.UserUpdatePasswordRequest;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Long register(UserCreateRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getUsername())
                .build();
        return userRepository.save(user).getId();
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return toResponse(user);
    }

    @Override
    public void updatePassword(Long id, UserUpdatePasswordRequest request) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 새로운 엔티티 생성 (id 동일, 나머지 정보 그대로, 비밀번호만 변경)
        User updatedUser = User.builder()
                .id(oldUser.getId())
                .email(oldUser.getEmail())
                .password(request.getPassword()) // 변경점
                .username(oldUser.getUsername())
                .grade(oldUser.getGrade())
                .role(oldUser.getRole())
                .createdAt(oldUser.getCreatedAt())
                .build();

        userRepository.save(updatedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다."));
        return toResponse(user);
    }

    @Override
    public UserResponse findByEmailAndName(String email, String username) {
        User user = userRepository.findByEmailAndUsername(email, username)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getGrade(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Override
    public UserResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if(!password.equals(user.getPassword()))
        {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return toResponse(user);
    }
}
