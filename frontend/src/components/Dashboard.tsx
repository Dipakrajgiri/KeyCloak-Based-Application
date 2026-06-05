"use client";

import { useState, useEffect, useCallback } from "react";
import { useAuth } from "@/context/AuthContext";
import { inventoryApi, categoryApi, itemApi } from "@/lib/api";
import { Navbar } from "./Navbar";
import { Modal } from "./Modal";

// ==================== Type Definitions ====================
interface Inventory {
  id: number;
  name: string;
  description: string;
  categoryCount: number;
  createdAt: string;
}

interface Category {
  id: number;
  name: string;
  description: string;
  inventoryId: number;
  itemCount: number;
  createdAt: string;
}

interface Item {
  id: number;
  name: string;
  description: string;
  quantity: number;
  price: number;
  categoryId: number;
  createdAt: string;
}

// ==================== View Types ====================
type View = "inventories" | "categories" | "items";

export function Dashboard() {
  const { } = useAuth();

  // Current view state
  const [view, setView] = useState<View>("inventories");
  const [selectedInventory, setSelectedInventory] = useState<Inventory | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);

  // Data state
  const [inventories, setInventories] = useState<Inventory[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState<"create" | "edit">("create");
  const [editTarget, setEditTarget] = useState<Inventory | Category | Item | null>(null);

  // ==================== Data Fetching ====================
  const fetchInventories = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await inventoryApi.getAll();
      setInventories(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load inventories");
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchCategories = useCallback(async (inventoryId: number) => {
    setLoading(true);
    setError("");
    try {
      const data = await categoryApi.getAll(inventoryId);
      setCategories(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load categories");
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchItems = useCallback(async (inventoryId: number, categoryId: number) => {
    setLoading(true);
    setError("");
    try {
      const data = await itemApi.getAll(inventoryId, categoryId);
      setItems(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load items");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchInventories();
  }, [fetchInventories]);

  // ==================== Navigation ====================
  const navigateToCategories = (inv: Inventory) => {
    setSelectedInventory(inv);
    setView("categories");
    fetchCategories(inv.id);
  };

  const navigateToItems = (cat: Category) => {
    setSelectedCategory(cat);
    setView("items");
    if (selectedInventory) {
      fetchItems(selectedInventory.id, cat.id);
    }
  };

  const navigateBack = () => {
    if (view === "items") {
      setView("categories");
      setSelectedCategory(null);
      if (selectedInventory) fetchCategories(selectedInventory.id);
    } else if (view === "categories") {
      setView("inventories");
      setSelectedInventory(null);
      fetchInventories();
    }
  };

  // ==================== CRUD Handlers ====================
  const handleCreate = () => {
    setModalMode("create");
    setEditTarget(null);
    setShowModal(true);
  };

  const handleEdit = (item: Inventory | Category | Item) => {
    setModalMode("edit");
    setEditTarget(item);
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this?")) return;
    try {
      if (view === "inventories") {
        await inventoryApi.delete(id);
        fetchInventories();
      } else if (view === "categories" && selectedInventory) {
        await categoryApi.delete(selectedInventory.id, id);
        fetchCategories(selectedInventory.id);
      } else if (view === "items" && selectedInventory && selectedCategory) {
        await itemApi.delete(selectedInventory.id, selectedCategory.id, id);
        fetchItems(selectedInventory.id, selectedCategory.id);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Delete failed");
    }
  };

  const handleModalSubmit = async (formData: Record<string, string | number>) => {
    try {
      if (view === "inventories") {
        if (modalMode === "create") {
          await inventoryApi.create(
            { name: formData.name as string, description: formData.description as string },
           
          );
        } else if (editTarget) {
          await inventoryApi.update(
            (editTarget as Inventory).id,
            { name: formData.name as string, description: formData.description as string },
           
          );
        }
        fetchInventories();
      } else if (view === "categories" && selectedInventory) {
        if (modalMode === "create") {
          await categoryApi.create(
            selectedInventory.id,
            { name: formData.name as string, description: formData.description as string },
           
          );
        } else if (editTarget) {
          await categoryApi.update(
            selectedInventory.id,
            (editTarget as Category).id,
            { name: formData.name as string, description: formData.description as string },
           
          );
        }
        fetchCategories(selectedInventory.id);
      } else if (view === "items" && selectedInventory && selectedCategory) {
        const itemData = {
          name: formData.name as string,
          description: formData.description as string,
          quantity: Number(formData.quantity) || 0,
          price: Number(formData.price) || 0,
        };
        if (modalMode === "create") {
          await itemApi.create(selectedInventory.id, selectedCategory.id, itemData);
        } else if (editTarget) {
          await itemApi.update(
            selectedInventory.id,
            selectedCategory.id,
            (editTarget as Item).id,
            itemData,
           
          );
        }
        fetchItems(selectedInventory.id, selectedCategory.id);
      }
      setShowModal(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Operation failed");
    }
  };

  // ==================== Render ====================
  const getTitle = () => {
    if (view === "items" && selectedCategory) return selectedCategory.name;
    if (view === "categories" && selectedInventory) return selectedInventory.name;
    return "My Inventories";
  };

  const getSubtitle = () => {
    if (view === "items") return "Items in this category";
    if (view === "categories") return "Categories in this inventory";
    return "Manage your inventory collections";
  };

  const getCreateLabel = () => {
    if (view === "items") return "+ New Item";
    if (view === "categories") return "+ New Category";
    return "+ New Inventory";
  };

  return (
    <div>
      <Navbar />
      <div className="container">
        {/* Breadcrumb */}
        {view !== "inventories" && (
          <div className="breadcrumb">
            <a href="#" onClick={(e) => { e.preventDefault(); setView("inventories"); setSelectedInventory(null); fetchInventories(); }}>
              Inventories
            </a>
            {selectedInventory && (
              <>
                <span className="sep">›</span>
                {view === "items" ? (
                  <a href="#" onClick={(e) => { e.preventDefault(); navigateBack(); }}>
                    {selectedInventory.name}
                  </a>
                ) : (
                  <span>{selectedInventory.name}</span>
                )}
              </>
            )}
            {selectedCategory && view === "items" && (
              <>
                <span className="sep">›</span>
                <span>{selectedCategory.name}</span>
              </>
            )}
          </div>
        )}

        {/* Page Header */}
        <div className="page-header">
          <div>
            {view !== "inventories" && (
              <button className="btn btn-ghost btn-sm" onClick={navigateBack} style={{ marginBottom: "0.5rem" }}>
                ← Back
              </button>
            )}
            <h1 className="page-title">{getTitle()}</h1>
            <p className="page-subtitle">{getSubtitle()}</p>
          </div>
          <button className="btn btn-primary" onClick={handleCreate}>
            {getCreateLabel()}
          </button>
        </div>

        {/* Error */}
        {error && (
          <div style={{ 
            background: "var(--danger-bg)", 
            border: "1px solid rgba(255,107,107,0.2)", 
            borderRadius: "var(--radius-sm)", 
            padding: "0.75rem 1rem", 
            marginBottom: "1rem",
            color: "var(--danger)",
            fontSize: "0.85rem"
          }}>
            {error}
          </div>
        )}

        {/* Loading */}
        {loading ? (
          <div style={{ textAlign: "center", padding: "3rem" }}>
            <div className="spinner" style={{ margin: "0 auto" }} />
          </div>
        ) : (
          <>
            {/* Stats */}
            {view === "inventories" && (
              <div className="stats-row">
                <div className="stat-card">
                  <div className="stat-value">{inventories.length}</div>
                  <div className="stat-label">Inventories</div>
                </div>
                <div className="stat-card">
                  <div className="stat-value">
                    {inventories.reduce((sum, inv) => sum + (inv.categoryCount || 0), 0)}
                  </div>
                  <div className="stat-label">Total Categories</div>
                </div>
              </div>
            )}

            {/* Content */}
            {view === "inventories" && (
              <div className="grid">
                {inventories.length === 0 ? (
                  <div className="empty-state" style={{ gridColumn: "1 / -1" }}>
                    <div className="icon">📦</div>
                    <h3>No inventories yet</h3>
                    <p>Create your first inventory to get started</p>
                    <button className="btn btn-primary" onClick={handleCreate}>
                      + Create Inventory
                    </button>
                  </div>
                ) : (
                  inventories.map((inv) => (
                    <div
                      key={inv.id}
                      className="card card-clickable inventory-card animate-in"
                      onClick={() => navigateToCategories(inv)}
                    >
                      <div className="card-header">
                        <h3 className="card-title">{inv.name}</h3>
                        <span className="badge badge-accent">
                          {inv.categoryCount || 0} categories
                        </span>
                      </div>
                      {inv.description && (
                        <p className="card-desc">{inv.description}</p>
                      )}
                      <div className="card-footer">
                        <span className="card-meta">
                          Created {new Date(inv.createdAt).toLocaleDateString()}
                        </span>
                        <div className="card-actions" onClick={(e) => e.stopPropagation()}>
                          <button className="btn btn-ghost btn-sm" onClick={() => handleEdit(inv)}>
                            ✏️
                          </button>
                          <button className="btn btn-ghost btn-sm" onClick={() => handleDelete(inv.id)}>
                            🗑️
                          </button>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}

            {view === "categories" && (
              <div className="grid">
                {categories.length === 0 ? (
                  <div className="empty-state" style={{ gridColumn: "1 / -1" }}>
                    <div className="icon">📁</div>
                    <h3>No categories yet</h3>
                    <p>Add categories to organize your items</p>
                    <button className="btn btn-primary" onClick={handleCreate}>
                      + Add Category
                    </button>
                  </div>
                ) : (
                  categories.map((cat) => (
                    <div
                      key={cat.id}
                      className="card card-clickable inventory-card animate-in"
                      onClick={() => navigateToItems(cat)}
                    >
                      <div className="card-header">
                        <h3 className="card-title">{cat.name}</h3>
                        <span className="badge badge-success">
                          {cat.itemCount || 0} items
                        </span>
                      </div>
                      {cat.description && (
                        <p className="card-desc">{cat.description}</p>
                      )}
                      <div className="card-footer">
                        <span className="card-meta">
                          Created {new Date(cat.createdAt).toLocaleDateString()}
                        </span>
                        <div className="card-actions" onClick={(e) => e.stopPropagation()}>
                          <button className="btn btn-ghost btn-sm" onClick={() => handleEdit(cat)}>
                            ✏️
                          </button>
                          <button className="btn btn-ghost btn-sm" onClick={() => handleDelete(cat.id)}>
                            🗑️
                          </button>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}

            {view === "items" && (
              <>
                {items.length === 0 ? (
                  <div className="empty-state">
                    <div className="icon">🏷️</div>
                    <h3>No items yet</h3>
                    <p>Add items to this category</p>
                    <button className="btn btn-primary" onClick={handleCreate}>
                      + Add Item
                    </button>
                  </div>
                ) : (
                  <div className="card" style={{ padding: 0, overflow: "hidden" }}>
                    <table className="item-table">
                      <thead>
                        <tr>
                          <th>Name</th>
                          <th>Description</th>
                          <th>Quantity</th>
                          <th>Price</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {items.map((item) => (
                          <tr key={item.id} className="animate-in">
                            <td className="item-name">{item.name}</td>
                            <td style={{ color: "var(--text-secondary)" }}>
                              {item.description || "—"}
                            </td>
                            <td className="item-qty">{item.quantity}</td>
                            <td className="item-price">
                              {item.price ? `$${item.price.toFixed(2)}` : "—"}
                            </td>
                            <td>
                              <div className="card-actions">
                                <button className="btn btn-ghost btn-sm" onClick={() => handleEdit(item)}>
                                  ✏️
                                </button>
                                <button className="btn btn-ghost btn-sm" onClick={() => handleDelete(item.id)}>
                                  🗑️
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </>
            )}
          </>
        )}

        {/* Modal */}
        {showModal && (
          <Modal
            view={view}
            mode={modalMode}
            editTarget={editTarget}
            onClose={() => setShowModal(false)}
            onSubmit={handleModalSubmit}
          />
        )}
      </div>
    </div>
  );
}
