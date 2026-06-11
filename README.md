# Fullstack React + Spring Boot SSO Project

This project demonstrates a fullstack application utilizing a React (Next.js) frontend and a Spring Boot backend, with Single Sign-On (SSO) integration using Keycloak.

## Project Structure
- `frontend/`: The Next.js React frontend.
- `backend/`: The Spring Boot Java backend.

## Keycloak Server Integration
This project uses a free Keycloak server provided by [Skycloak](https://skycloak.io/). However, you are not restricted to Skycloak. You can use any Keycloak server of your own (e.g., a local Docker instance or another cloud provider) by simply updating the environment variables in the frontend and backend `.env` files.

## Setup Instructions
1. Navigate to the `backend/` and `frontend/` directories respectively.
2. Copy the `.env.example` file to `.env` in both directories.
3. Fill in your own configuration details (e.g., Keycloak URLs, Client IDs, Database credentials) in the `.env` files.
4. Run each module according to the instructions in their respective READMEs.

Please see `frontend/README.md` and `backend/README.md` for specific module setup instructions.

## Repository Versions / Git Tags
This repository demonstrates different stages of implementing Keycloak security. You can checkout specific Git tags to see the code at different phases of the project:

### 1. `V1KeyclaokAuthenticationOnly`
- **Focus**: Basic Authentication using the Backend-For-Frontend (BFF) Pattern.
- **What it does**: Integrates Spring Security OAuth2 Client and Resource Server to authenticate users. The frontend redirects to the backend, which redirects to Keycloak for login. The backend uses session cookies to authenticate frontend requests.
- **Authorization Level**: Very basic. Users can view/edit items based solely on hardcoded database logic (e.g., filtering queries by `userId` in Postgres). Keycloak is only used to verify *who* the user is, not *what* they can do.
- **How to checkout**: `git checkout V1KeyclaokAuthenticationOnly`

### 2. `AuthorizationWithUMA`
- **Focus**: Advanced Fine-Grained Authorization using Keycloak UMA 2.0 (User-Managed Access) & the Protection API.
- **What it does**: The backend acts as a dynamic Resource Server. Every time a user creates an inventory, the backend automatically registers it as a protected resource in Keycloak using its Protection API Token (PAT). Users are set as the **resource owners** in Keycloak, meaning they can share their resources directly from the Keycloak Account Console.
- **Authorization Level**: Advanced. Instead of relying solely on DB queries, the backend uses `keycloak-authz-client` to ask Keycloak for permission (evaluating Requesting Party Tokens - RPT) whenever someone tries to edit or delete a resource. Owners bypass the check via backend logic, while non-owners must be explicitly granted permission via Keycloak UMA policies.
- **How to checkout**: `git checkout AuthorizationWithUMA`

### 3. `clientAuthorization` (Current)
- **Focus**: Centralized **Client-Managed Authorization** — eliminating User-Managed Access (UMA) entirely in favor of full backend control.
- **What it does**: All Keycloak resources are now owned by the **backend client (`inventory-backend`)**, not the end-user. Users can no longer share resources directly from the Keycloak Account Console. Instead, the application owner uses an in-app **Share Modal** to delegate access to other users. The backend then uses a **Protection API Token (PAT)** (obtained via Client Credentials flow) to programmatically create UMA policies in Keycloak on the user's behalf.
- **Authorization Level**: Enterprise-Grade. The backend is both a **Policy Enforcement Point (PEP)** and a **Policy Administration Point (PAP)**. Ownership is tracked in PostgreSQL (for fast lookup), while fine-grained access control is enforced via Keycloak's Protection API. Non-owners' permissions are validated by requesting an RPT (Requesting Party Token) from Keycloak at runtime.
- **Key Changes from `AuthorizationWithUMA`**:
  - Resources are registered with `owner = Client` (not the logged-in user).
  - `ownerManagedAccess = true` so the Client (not the user) can manage policies programmatically.
  - New `POST /api/inventories/{id}/share` endpoint for in-app permission delegation.
  - All controllers refactored from `@RegisteredOAuth2AuthorizedClient` to `OAuth2AuthorizedClientService` to prevent session-related redirect exceptions.
- **How to checkout**: `git checkout clientAuthorization`
