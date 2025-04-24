import React from "react";
import "./Modal.css";

export default function Modal({ item, onClose, onAddToCart }) {
  if (!item) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* 닫기 버튼 */}
        <button className="close-button" onClick={onClose}>×</button>

        <img src={item.image} alt={item.name} className="modal-image" />
        <h2 className="modal-name">{item.name}</h2>
        <p className="modal-description">{item.description}</p>
        <p className="modal-price">₩{item.price.toLocaleString()}</p>

        {/* 장바구니 추가 버튼 */}
        <button className="modal-add-button" onClick={() => onAddToCart(item)}>
          장바구니 추가
        </button>
      </div>
    </div>
  );
}