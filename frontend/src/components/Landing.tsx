"use client";

import { useAuth } from "@/context/AuthContext";

export function Landing() {
  const { login, register } = useAuth();

  return (
    <div className="hero">
      <h1 className="hero-title">Inventory Manager</h1>
      <p className="hero-subtitle">
        A modern inventory management system built with Next.js, Spring Boot,
        and Keycloak SSO authentication. Organize your items with ease.
      </p>
      <div className="hero-actions">
        <button className="btn btn-primary btn-lg" onClick={login}>
          ✦ Sign In
        </button>
        <button className="btn btn-secondary btn-lg" onClick={register}>
          Create Account
        </button>
      </div>

      <div className="hero-features">
        <div className="hero-feature animate-in">
          <div className="icon">🔐</div>
          <h4>Secure Auth</h4>
          <p>Enterprise-grade SSO with Keycloak & OAuth2</p>
        </div>
        <div className="hero-feature animate-in" style={{ animationDelay: "0.1s" }}>
          <div className="icon">📦</div>
          <h4>Organize</h4>
          <p>Inventories, categories, and items — all structured</p>
        </div>
        <div className="hero-feature animate-in" style={{ animationDelay: "0.2s" }}>
          <div className="icon">⚡</div>
          <h4>Fast API</h4>
          <p>Spring Boot backend with REST API & JWT tokens</p>
        </div>
      </div>
    </div>
  );
}
