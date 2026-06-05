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
