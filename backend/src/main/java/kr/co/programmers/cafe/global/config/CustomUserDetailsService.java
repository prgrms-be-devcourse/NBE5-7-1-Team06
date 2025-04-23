package kr.co.programmers.cafe.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetails userDetails;

    // yml 파일 기반 인증 정보를 담은 UserDetails 생성
    public CustomUserDetailsService(@Value("${spring.security.user.name}") String username,
                                    @Value("${spring.security.user.password}") String password,
                                    @Value("${spring.security.user.roles}") String roles) {
        log.info("roles = {}", roles);
        this.userDetails = User.builder()
                .username(username)
                .password(new BCryptPasswordEncoder().encode(password))
                .roles(roles)
                .build();
    }

    // yml 파일 기반 인증 정보를 만들기 위해 Override
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!userDetails.getUsername().equals(username)) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            log.warn("ADMIN 권한 부족");
            throw new UsernameNotFoundException("권한이 부족합니다.");  // 로그인 실패 처리됨
        }
        log.info("Authorities = {}", userDetails.getAuthorities());
        return userDetails;
    }
}