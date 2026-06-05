"use client";

import { useState, useEffect } from "react";

interface ModalProps {
  view: "inventories" | "categories" | "items";
  mode: "create" | "edit";
  editTarget: Record<string, unknown> | null;
  onClose: () => void;
  onSubmit: (data: Record<string, string | number>) => void;
}

export function Modal({ view, mode, editTarget, onClose, onSubmit }: ModalProps) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [quantity, setQuantity] = useState("0");
  const [price, setPrice] = useState("0");

  useEffect(() => {
    if (mode === "edit" && editTarget) {
      setName((editTarget.name as string) || "");
      setDescription((editTarget.description as string) || "");
      if (view === "items") {
        setQuantity(String(editTarget.quantity || 0));
        setPrice(String(editTarget.price || 0));
      }
    }
  }, [mode, editTarget, view]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const data: Record<string, string | number> = { name, description };
    if (view === "items") {
      data.quantity = Number(quantity);
      data.price = Number(price);
    }
    onSubmit(data);
  };

  const getTitle = () => {
    const entity = view === "inventories" ? "Inventory" : view === "categories" ? "Category" : "Item";
    return `${mode === "create" ? "Create" : "Edit"} ${entity}`;
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal-title">{getTitle()}</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Name</label>
            <input
              className="form-input"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Enter name..."
              required
              autoFocus
            />
          </div>
          <div className="form-group">
            <label className="form-label">Description</label>
            <textarea
              className="form-input"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Optional description..."
            />
          </div>
          {view === "items" && (
            <>
              <div className="form-group">
                <label className="form-label">Quantity</label>
                <input
                  className="form-input"
                  type="number"
                  min="0"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Price ($)</label>
                <input
                  className="form-input"
                  type="number"
                  min="0"
                  step="0.01"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                />
              </div>
            </>
          )}
          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              {mode === "create" ? "Create" : "Save Changes"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
