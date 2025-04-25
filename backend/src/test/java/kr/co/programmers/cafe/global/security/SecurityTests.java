package kr.co.programmers.cafe.global.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String ADMIN_USERNAME = "example";   // 테스트 yml 에 작성한 값 입력하시면 됩니다.
    private static final String ADMIN_PASSWORD = "example";   // 테스트 yml 에 작성한 값 입력하시면 됩니다.

    private static final String LOGIN_URL = "/admin/login";
    private static final String ADMIN_MAIN = "/admin";

    // 내부 IP + ADMIN 권한 → 성공
    @Test
    void internalIpWithAdminRole_shouldSucceed() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", ADMIN_USERNAME)
                        .param("password", ADMIN_PASSWORD)
                        .with(csrf())
                        .with(request -> {
                            request.setRemoteAddr("127.0.0.1");
                            return request;
                        }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ADMIN_MAIN));
    }

    // 내부 IP + USER 권한 → 실패 (Spring Security 자체 로직 실패 → login?error 리다이렉트)
    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = "USER")
    void internalIpWithUserRole_shouldFail() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .with(request -> {
                            request.setRemoteAddr("127.0.0.1");
                            return request;
                        }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL + "?error=login_failed"));
    }

    // 외부 IP + USER 권한 → 실패
    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = "USER")
    void externalIpWithUserRole_shouldFail() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .with(request -> {
                            request.setRemoteAddr("192.168.1.1");
                            return request;
                        }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL + "?error=ip_blocked"));
    }

    // 외부 IP + ADMIN 권한 → 실패 (IP 차단으로 manage 접근 불가)
    @Test
    void externalIpWithAdminRole_shouldFail() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", ADMIN_USERNAME)
                        .param("password", ADMIN_PASSWORD)
                        .with(csrf())
                        .with(request -> {
                            request.setRemoteAddr("192.168.1.1");
                            return request;
                        }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL + "?error=ip_blocked"));
    }
}
