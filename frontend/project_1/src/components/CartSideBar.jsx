import React, { useState } from "react";
import api from "../api/axios";

import "./CartSideBar.css";
import PostCodeModal from "./PostCodeModal";

export default function CartSideBar({ cartItems, onUpdateQuantity, onClose }) {
    const [email, setEmail] = useState("");
    const [address, setAddress] = useState("");
    const [addressDetail, setAddressDetail] = useState("");
    const [zipcode, setZipcode] = useState("");
    const [isPopupOpen, setIsPopupOpen] = useState(false);

      
    // 총 금액 계산
    const totalPrice = cartItems.reduce(
        (acc, item) => acc + item.price * item.quantity,
        0
      );
  
    const handleCheckout = async () => {
      if(!cartItems || cartItems.length === 0) {
        alert("구매할 상품을 선택해주세요.")
        return;
      }

      if (!isValidEmail(email)) {
        alert("올바른 이메일 주소를 입력해주세요.");
        return;
      }

      if (!zipcode || !address || !addressDetail) {
        alert("우편번호, 주소, 상세주소를 모두 입력해주세요.");
        return;
      }

      const orderData = {
        email,
        address: address + " " + addressDetail,
        zipCode: zipcode,
        totalPrice,
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

    // 이메일 유효성 체크
    const isValidEmail = (email) => {
      const regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
      return regex.test(email);
    };
  
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
            placeholder="우편번호 입력"
            value={zipcode}
            onClick={() => setIsPopupOpen(true)}
            readOnly
          />
          <input
            type="text"
            placeholder="주소 입력"
            value={address}
            onClick={() => setIsPopupOpen(true)}
            readOnly
          />
          <input
            type="text"
            placeholder="상세 주소 입력"
            value={addressDetail}
            onChange={(e) => setAddressDetail(e.target.value)}
          />
          {isPopupOpen && (
            <PostCodeModal
              onClose={() => setIsPopupOpen(false)}
              onComplete={(data) => {
                setAddress(data.address);
                setZipcode(data.zonecode);
                setIsPopupOpen(false);
              }}
            />
          )}
        </div>
  
        <button className="checkout-button" onClick={handleCheckout}>
          결제하기
        </button>
      </div>
    );
  }