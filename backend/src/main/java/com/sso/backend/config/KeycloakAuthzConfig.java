package com.sso.backend.config;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@org.springframework.context.annotation.Configuration
public class KeycloakAuthzConfig {

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Bean
    public AuthzClient authzClient() {
        int realmsIndex = issuerUri.indexOf("/realms/");
        String authServerUrl = issuerUri.substring(0, realmsIndex);
        String realm = issuerUri.substring(realmsIndex + "/realms/".length());

        Configuration config = new Configuration(
                authServerUrl,
                realm,
                clientId,
                Map.of("secret", clientSecret),
                null
        );

        return AuthzClient.create(config);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
