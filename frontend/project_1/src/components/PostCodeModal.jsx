import React from "react";
import "./PostCodeModal.css";
import DaumPostCode from "react-daum-postcode"

export default function PostCodeModal({ onClose, onComplete }) {
    const postCodeStyle = {
        width: '100%',
        height: '100%',
    };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* 닫기 버튼 */}
        <button className="close-button" onClick={onClose}>×</button>
        <DaumPostCode
            style={postCodeStyle}
            onClose={onClose}
            onComplete={onComplete}
        />
      </div>
    </div>
  );
}