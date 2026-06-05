package com.sso.backend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Custom converter that extracts Keycloak roles from the JWT token.
 *
 * KEYCLOAK JWT STRUCTURE:
 * A typical Keycloak JWT contains roles in two places:
 *
 * 1. realm_access.roles — roles assigned at the realm level
 *    Example: {"realm_access": {"roles": ["admin", "user", "default-roles-sso-realm"]}}
 *
 * 2. resource_access.<client-id>.roles — roles specific to a client
 *    Example: {"resource_access": {"sso-backend": {"roles": ["manage-users"]}}}
 *
 * This converter extracts BOTH and converts them into Spring Security authorities.
 */
@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Extract realm-level roles
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null) {
            return Collections.emptyList();
        }

        // Convert each role to a Spring Security authority with "ROLE_" prefix
        // This allows us to use @PreAuthorize("hasRole('admin')") in controllers
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
