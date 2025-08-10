package com.pro.project01.v2.domain.user.service;

import com.pro.project01.v2.domain.user.dto.UserLoginRequest;
import com.pro.project01.v2.domain.user.dto.UserRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.dto.UserUpdateRequest;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ 빈 주입

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getGrade(),
                user.getRole()
        );
    }

    @Override
    public UserResponse register(UserRequest request) {
        String username = request.username().trim();
        String email    = request.email().trim().toLowerCase(); // ✅ 정규화

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.create(
                username,
                email,
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);
        return toResponse(user);
    }

    private boolean isBcrypt(String pwd) { // ✅ NPE/우선순위 수정
        if (pwd == null) return false;
        return pwd.startsWith("$2a$") || pwd.startsWith("$2b$") || pwd.startsWith("$2y$");
    }

    @Override
    public UserResponse login(UserLoginRequest req) {
        String username = req.username().trim();

        var u = userRepository.findByUsername(username).orElse(null);
        if (u == null) return null;

        boolean ok = isBcrypt(u.getPassword())
                ? passwordEncoder.matches(req.password(), u.getPassword())
                : req.password().equals(u.getPassword()); // 과거 평문 마이그레이션 고려

        if (!ok) return null;

        // 과거 평문이면 이번 로그인에 해시로 교체
        if (!isBcrypt(u.getPassword())) {
            u.changePassword(passwordEncoder.encode(req.password()));
        }

        return toResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return toResponse(user);
    }

    @Override
    public UserResponse updateMyInfo(Long id, UserUpdateRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.email() != null && !request.email().isBlank()) {
            String newEmail = request.email().trim().toLowerCase(); // ✅ 정규화
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            user.changeEmail(newEmail);
        }

        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            user.changePassword(passwordEncoder.encode(request.newPassword()));
        }

        return toResponse(user);
    }

    @Override
    public String findUsernameByEmail(String email) {
        String key = email.trim().toLowerCase(); // ✅ 정규화
        return userRepository.findByEmail(key).map(User::getUsername).orElse(null);
    }

    @Override
    public String resetPasswordToTemp(String email) {
        String key = email.trim().toLowerCase(); // ✅ 정규화
        var user = userRepository.findByEmail(key)
                .orElseThrow(() -> new IllegalArgumentException("이메일로 가입된 사용자가 없습니다."));
        String temp = UUID.randomUUID().toString().substring(0, 10);
        user.changePassword(passwordEncoder.encode(temp));
        return temp;
    }
}
