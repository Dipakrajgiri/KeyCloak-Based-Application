package com.sso.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/sso-realm",
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/sso-realm/protocol/openid-connect/certs"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
    }
}
