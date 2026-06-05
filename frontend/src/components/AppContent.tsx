"use client";

import { useAuth } from "@/context/AuthContext";
import { Landing } from "./Landing";
import { Dashboard } from "./Dashboard";

export function AppContent() {
  const { authenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <p style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>
          Connecting to authentication server...
        </p>
      </div>
    );
  }

  if (!authenticated) {
    return <Landing />;
  }

  return <Dashboard />;
}
