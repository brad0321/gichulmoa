package com.pro.project01.v2.domain.user.service;

import com.pro.project01.v2.domain.user.dto.UserLoginRequest;
import com.pro.project01.v2.domain.user.dto.UserRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.dto.UserUpdateRequest;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

   private UserResponse toResponse(User user)
   {
       return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getGrade(), user.getRole());
   }

    @Override
    public UserResponse register(UserRequest request) {
        if (userRepository.existsByUsername(request.username()))
        {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.email()))
        {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.create(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);

        return toResponse(user);
    }

    boolean isBcrypt(String pwd) {
        return pwd != null && pwd.startsWith("$2a$") || pwd.startsWith("$2b$") || pwd.startsWith("$2y$");
    }

    @Override
    public UserResponse login(UserLoginRequest req) {
        var u = userRepository.findByUsername(req.username()).orElse(null);
        if (u == null) return null;

        boolean ok = isBcrypt(u.getPassword())
                ? passwordEncoder.matches(req.password(), u.getPassword())
                : req.password().equals(u.getPassword()); // 기존 평문

        if (!ok) return null;

        // 평문이었다면 이번 로그인에 해시로 교체
        if (!isBcrypt(u.getPassword())) {
            u.changePassword(passwordEncoder.encode(req.password()));
        }
        return toResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return toResponse(user);
    }

    @Override
    public UserResponse updateMyInfo(Long id, UserUpdateRequest request) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if(request.email() != null && !request.email().isBlank())
        {
            if(userRepository.existsByEmail(request.email()) && !request.email().equals(user.getEmail()))
            {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            user.changeEmail(request.email());
        }

        if(request.newPassword() != null && !request.newPassword().isBlank())
        {
            user.changePassword(passwordEncoder.encode(request.newPassword()));
        }

        return toResponse(user);
    }

    @Override
    public String findUsernameByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getUsername).orElse(null);
    }

    @Override
    public String resetPasswordToTemp(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("이메일로 가입된 사용자가 없습니다."));
        String temp = UUID.randomUUID().toString().substring(0, 10);
        user.changePassword(passwordEncoder.encode(temp));

        return temp;
    }
}
