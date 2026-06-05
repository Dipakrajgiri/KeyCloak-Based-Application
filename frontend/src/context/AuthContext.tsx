"use client";

import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import { authApi } from "@/lib/api";

interface UserInfo {
  id: string;
  username: string;
  email: string;
  name: string;
  roles: string[];
}

interface AuthContextType {
  authenticated: boolean;
  loading: boolean;
  userInfo: UserInfo | null;
  login: () => void;
  logout: () => void;
  register: () => void;
}

const AuthContext = createContext<AuthContextType>({
  authenticated: false,
  loading: true,
  userInfo: null,
  login: () => {},
  logout: () => {},
  register: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);

  useEffect(() => {
    // Check if user is authenticated with the backend (via session cookie)
    authApi.getMe()
      .then((data) => {
        if (data) {
          setAuthenticated(true);
          setUserInfo(data);
        } else {
          setAuthenticated(false);
        }
      })
      .catch(() => {
        setAuthenticated(false);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const login = useCallback(() => {
    window.location.href = `${process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080"}/oauth2/authorization/keycloak`;
  }, []);

  const register = useCallback(() => {
    // Keycloak standard registration is handled on the Keycloak server itself.
    // In a BFF architecture, we redirect to the same authorization endpoint.
    // The user will click "Register" on the Keycloak login screen (requires User Registration enabled in Keycloak).
    window.location.href = `${process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080"}/oauth2/authorization/keycloak`;
  }, []);

  const logout = useCallback(() => {
    window.location.href = `${process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080"}/api/auth/logout`;
  }, []);

  return (
    <AuthContext.Provider
      value={{ authenticated, loading, userInfo, login, logout, register }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
