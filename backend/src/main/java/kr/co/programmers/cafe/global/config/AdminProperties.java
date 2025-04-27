package kr.co.programmers.cafe.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.user")
@Getter
@Setter
public class AdminProperties {
    private String name;
    private String password;
    private String roles;
}
