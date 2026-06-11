/**
 * API Service — handles all HTTP requests to the Spring Boot backend
 *
 * Using the BFF pattern, we rely on the backend session cookie (JSESSIONID).
 * The browser automatically includes cookies when `credentials: "include"` is set.
 */

const API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

async function apiRequest(endpoint: string, options: RequestInit = {}) {
  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    credentials: "include", // This is crucial for BFF: sends the session cookie
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (response.status === 401) {
    // If we get unauthorized, force a reload to trigger the login flow
    window.location.href = `${API_URL}/oauth2/authorization/keycloak`;
    return null;
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: "Request failed" }));
    throw new Error(error.error || `HTTP ${response.status}`);
  }

  // Handle 204 No Content (delete responses)
  if (response.status === 204) return null;

  return response.json();
}

// ==================== Inventory API ====================

export const inventoryApi = {
  getAll: () =>
    apiRequest("/api/inventories"),

  getOne: (id: number) =>
    apiRequest(`/api/inventories/${id}`),

  create: (data: { name: string; description?: string }) =>
    apiRequest("/api/inventories", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  update: (id: number, data: { name: string; description?: string }) =>
    apiRequest(`/api/inventories/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),

  delete: (id: number) =>
    apiRequest(`/api/inventories/${id}`, { method: "DELETE" }),

  share: (id: number, data: { targetUserId: string; scopes: string[] }) =>
    apiRequest(`/api/inventories/${id}/share`, {
      method: "POST",
      body: JSON.stringify(data),
    }),
};

// ==================== Category API ====================

export const categoryApi = {
  getAll: (inventoryId: number) =>
    apiRequest(`/api/inventories/${inventoryId}/categories`),

  getOne: (inventoryId: number, id: number) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${id}`),

  create: (inventoryId: number, data: { name: string; description?: string }) =>
    apiRequest(`/api/inventories/${inventoryId}/categories`, {
      method: "POST",
      body: JSON.stringify(data),
    }),

  update: (inventoryId: number, id: number, data: { name: string; description?: string }) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),

  delete: (inventoryId: number, id: number) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${id}`, { method: "DELETE" }),
};

// ==================== Item API ====================

export const itemApi = {
  getAll: (inventoryId: number, categoryId: number) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${categoryId}/items`),

  getOne: (inventoryId: number, categoryId: number, id: number) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${categoryId}/items/${id}`),

  create: (
    inventoryId: number,
    categoryId: number,
    data: { name: string; description?: string; quantity: number; price?: number }
  ) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${categoryId}/items`, {
      method: "POST",
      body: JSON.stringify(data),
    }),

  update: (
    inventoryId: number,
    categoryId: number,
    id: number,
    data: { name: string; description?: string; quantity: number; price?: number }
  ) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${categoryId}/items/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),

  delete: (inventoryId: number, categoryId: number, id: number) =>
    apiRequest(`/api/inventories/${inventoryId}/categories/${categoryId}/items/${id}`, {
      method: "DELETE",
    }),
};

// ==================== Auth API ====================

export const authApi = {
  getMe: () => apiRequest("/api/auth/me"),
};
