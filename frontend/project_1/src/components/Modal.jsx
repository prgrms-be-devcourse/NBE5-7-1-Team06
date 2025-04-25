import React from "react";
import "./Modal.css";

export default function Modal({ item, onClose, onAddToCart }) {
  if (!item) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* 닫기 버튼 */}
        <button className="close-button" onClick={onClose}>×</button>

        <img 
          //src={`http://localhost:8080/api/items/${item.id}/images`} 
          src={`http://localhost:3000/images/item-${item.id}.jpg`}
          alt={item.name} 
          className="modal-image"   
        //아직 서버에서 값을 가져올 수 없기 때문에 대체 이미지 경로
          onError={(e) => {
            e.target.src = "/images/coffee1.jpg"; }}/>
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