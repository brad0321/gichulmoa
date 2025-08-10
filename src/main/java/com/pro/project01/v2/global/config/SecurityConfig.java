package com.pro.project01.v2.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ★ 현재 단계: 컨트롤러 주도 세션 로그인(직접 세션에 loginUser 저장)
    //   - Spring Security가 /login, /logout 가로채지 않도록 formLogin/logout 비활성화
    //   - 접근 제어는 컨트롤러에서 redirect로 처리(게스트 허용 등)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CORS: 같은 도메인(8080)에서 Thymeleaf 쓰면 사실 필요 없음. 남겨두되 범위 최소화
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration c = new CorsConfiguration();
                    // 프론트가 별도 포트/도메인일 때만 추가 (예: Vite 5173 등)
                    c.setAllowedOrigins(List.of("http://localhost:8080"));
                    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                    c.setAllowedHeaders(List.of("*"));
                    c.setAllowCredentials(true);
                    return c;
                }))

                // CSRF: 컨트롤러가 직접 폼 처리 + API 혼용이면 우선 비활성
                .csrf(csrf -> csrf.disable())

                // 세션: 컨트롤러에서 HttpSession 쓰므로 필요 시 생성
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // 인가 규칙: 현재는 컨트롤러에서 접근제어(redirect)하므로 전반 permitAll 권장
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/health",
                                "/login", "/logout",
                                "/guest/**",
                                "/members/**",
                                "/dashboard",
                                "/css/**","/js/**","/img/**","/fonts/**","/vendor/**"
                        ).permitAll()
                        // 공개 API 예시
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        // 나머지도 당장은 허용 (컨트롤러에서 세션 체크)
                        .anyRequest().permitAll()
                )

                // ★ 중요: Spring Security의 폼로그인/로그아웃을 꺼서 컨트롤러와 충돌 방지
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())

                // 디버그용
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
