"use client";

import { useAuth } from "@/context/AuthContext";

export function Navbar() {
  const { userInfo, logout } = useAuth();

  const initials = userInfo?.name
    ? userInfo.name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
    : userInfo?.username?.[0]?.toUpperCase() || "?";

  return (
    <nav className="navbar">
      <a href="/" className="navbar-brand">
        <div className="icon">📦</div>
        <span>Inventory Manager</span>
      </a>

      <div className="navbar-user">
        <div className="user-info">
          <div className="user-name">{userInfo?.name || userInfo?.username}</div>
          <div className="user-email">{userInfo?.email}</div>
        </div>
        <div className="user-avatar">{initials}</div>
        <button className="btn btn-secondary btn-sm" onClick={logout}>
          Logout
        </button>
      </div>
    </nav>
  );
}
