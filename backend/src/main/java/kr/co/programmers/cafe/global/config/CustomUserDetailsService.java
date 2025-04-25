package kr.co.programmers.cafe.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final AdminProperties adminProperties;

    // yml 파일 기반 인증 정보를 담은 UserDetails 생성
    public CustomUserDetailsService(AdminProperties adminProperties, PasswordEncoder encoder) {
        this.encoder = encoder;
        this.adminProperties = adminProperties;
    }

    // yml 파일 기반 인증 정보를 만들기 위해 Override
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.equals(adminProperties.getName())) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        return User.builder()
                .username(adminProperties.getName())
                .password(encoder.encode(adminProperties.getPassword()))
                .roles(adminProperties.getRoles())
                .build();
    }
}