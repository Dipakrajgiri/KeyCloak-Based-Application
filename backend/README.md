# Backend Application

This is a Spring Boot REST API backend that secures its endpoints using OAuth2 resource server integration with Keycloak.

## Environment Setup
Before starting the backend, you must set up your environment variables. Never commit actual credentials to version control.
1. Copy the `.env.example` file to a new file named `.env`:
   ```bash
   cp .env.example .env
   ```
2. Provide your Keycloak credentials, JWKS URI, and Database configuration details. This project uses Skycloak for Keycloak hosting by default, but you can use any Keycloak server by changing the `KEYCLOAK_ISSUER_URI` and `KEYCLOAK_JWKS_URI` values.

## Running the Application
To build and run the Spring Boot application:
```bash
./mvnw spring-boot:run
```
The backend server typically runs on port `8080`.
