import React, { useState } from "react";
import api from "../api/axios";
import "./CartSideBar.css";

export default function CartSideBar({ cartItems, onUpdateQuantity, onClose }) {
    const [email, setEmail] = useState("");
    const [address, setAddress] = useState("");
    const [zipcode, setZipcode] = useState("");
  
    const handleCheckout = async () => {
      const orderData = {
        email,
        address,
        zipCode: zipcode,
        orderItemRequests: cartItems.map(item => ({
          itemId: item.id,
          quantity: item.quantity,
        })),
      };
  
      try {
        const response = await api.post("/orders", orderData);
        alert("주문이 완료되었습니다!");
        console.log("서버 응답:", response.data);
      } catch (error) {
        console.error("주문 실패:", error);
        alert("주문 처리 중 오류가 발생했습니다.");
      }
    };
  
    // 총 금액 계산
    const totalPrice = cartItems.reduce(
      (acc, item) => acc + item.price * item.quantity,
      0
    );
  
    return (
      <div className="cart-sidebar">
        <div className="cart-header">
          <h2>장바구니</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
  
        <div className="cart-items">
          {cartItems.length === 0 ? (
            <p className="empty-cart">장바구니가 비어 있습니다.</p>
          ) : (
            cartItems.map(item => (
              <div key={item.id} className="cart-item">
                <span className="item-name">{item.name}</span>
                <div className="item-controls">
                  <button onClick={() => onUpdateQuantity(item.id, -1)}>-</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => onUpdateQuantity(item.id, 1)}>+</button>
                </div>
                <span className="item-price">
                  ₩{(item.price * item.quantity).toLocaleString()}
                </span>
              </div>
            ))
          )}
        </div>
  
        {/* 총 금액 표시*/}
        {cartItems.length > 0 && (
          <div className="total-price">
            <strong>총 결제 금액:</strong>{" "}
            <span>₩{totalPrice.toLocaleString()}</span>
          </div>
        )}
  
        {/* 사용자 정보 입력 */}
        <div className="user-info-form">
          <input
            type="email"
            placeholder="이메일 입력"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="text"
            placeholder="주소 입력"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
          />
          <input
            type="text"
            placeholder="우편번호 입력"
            value={zipcode}
            onChange={(e) => setZipcode(e.target.value)}
          />
        </div>
  
        <button className="checkout-button" onClick={handleCheckout}>
          결제하기
        </button>
      </div>
    );
  }