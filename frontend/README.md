# Frontend Application

This is a React (Next.js) frontend application that integrates with a Keycloak SSO server and talks to the Spring Boot backend.

## Environment Setup
Before starting the application, you must set up your environment variables. 
1. Copy the `.env.example` file to a new file named `.env.local` or `.env`:
   ```bash
   cp .env.example .env.local
   ```
2. Update the Keycloak configuration to point to your Keycloak server. This project comes configured for Skycloak out of the box, but you can use any Keycloak server of your own.

## Running the Application
To run the development server:
```bash
npm install
npm run dev
```
Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.
