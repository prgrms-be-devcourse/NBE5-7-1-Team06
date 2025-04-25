import React, { useState, useEffect } from "react";
import api from "../api/axios";
import Modal from "../components/Modal.jsx";
import CartSideBar from "../components/CartSideBar.jsx";
import "./MainPage.css";

export default function MainPage() {
  const CATEGORY_LIST = ["BEAN", "TEA", "GOODS", "OTHER"];
  const [items, setItems] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(CATEGORY_LIST[0]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [cartItems, setCartItems] = useState([]);
  const [cartVisible, setCartVisible] = useState(false);


  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await api.get("/items/all"); 
        setItems(response.data);
      } catch (error) {
        console.error("메뉴 데이터 불러오기 실패:", error);

        const dummyItems = [
          // BEAN
          { id: 1, name: "Americano", price: 3000, category: "BEAN", image: "/images/coffee1.jpg", description: "진한 에스프레소와 시원한 물의 조화" },
          { id: 2, name: "Latte", price: 3500, category: "BEAN", image: "/images/coffee2.png", description: "우유와 커피의 부드러운 만남" },
          { id: 3, name: "Cappuccino", price: 3700, category: "BEAN", image: "/images/cappuccino.jpg", description: "부드러운 거품이 매력적인 커피" },
          { id: 4, name: "Espresso", price: 2800, category: "BEAN", image: "/images/espresso.jpg", description: "깊고 진한 원샷 에스프레소" },
        
          // TEA
          { id: 5, name: "Green Tea", price: 3800, category: "TEA", image: "/images/green-tea.jpg", description: "향긋한 녹차" },
          { id: 6, name: "Black Tea", price: 4000, category: "TEA", image: "/images/black-tea.jpg", description: "진한 향과 맛의 홍차" },
          { id: 7, name: "Chamomile", price: 4200, category: "TEA", image: "/images/chamomile.jpg", description: "마음을 안정시켜주는 캐모마일" },
          { id: 8, name: "Lemon Tea", price: 3900, category: "TEA", image: "/images/lemon-tea.jpg", description: "상큼한 레몬이 더해진 홍차" },
        
          // GOODS
          { id: 9, name: "Mug", price: 12000, category: "GOODS", image: "/images/mug.jpg", description: "Grids & Circles 전용 머그컵" },
          { id: 10, name: "Tumbler", price: 15000, category: "GOODS", image: "/images/tumbler.jpg", description: "텀블러로 환경도 지켜요" },
          { id: 11, name: "Eco Bag", price: 9000, category: "GOODS", image: "/images/ecobag.jpg", description: "감성 넘치는 에코백" },
          { id: 12, name: "Sticker Pack", price: 3000, category: "GOODS", image: "/images/stickers.jpg", description: "G&C 감성 스티커 세트" },
        
          // OTHER
          { id: 13, name: "Gift Card", price: 5000, category: "OTHER", image: "/images/giftcard.jpg", description: "선물용 카드" },
          { id: 14, name: "Coupon 5%", price: 0, category: "OTHER", image: "/images/coupon.jpg", description: "다음 방문 시 5% 할인 쿠폰" },
          { id: 15, name: "Membership", price: 20000, category: "OTHER", image: "/images/membership.jpg", description: "1년 멤버십 등록" },
          { id: 16, name: "Note", price: 2500, category: "OTHER", image: "/images/note.jpg", description: "귀여운 디자인의 메모장" }
        ];
          setItems(dummyItems);

        }
      };
    fetchData();
  }, []);

  const filteredItems = items.filter(item => item.category === selectedCategory);

  const handleAddToCart = (item) => {
    setCartItems(prev => {
      const existing = prev.find(i => i.id === item.id);
      return existing
        ? prev.map(i => i.id === item.id ? { ...i, quantity: i.quantity + 1 } : i)
        : [...prev, { ...item, quantity: 1 }];
    });
    setCartVisible(true);
    setSelectedItem(null);
  };

  const handleUpdateQuantity = (id, delta) => {
    setCartItems(prev =>
      prev.map(item =>
        item.id === id ? { ...item, quantity: item.quantity + delta } : item
      ).filter(item => item.quantity > 0)
    );
  };

  return (
    <div className="main-container">
      <h1 className="title">Grids & Circles</h1>

      <div className="category-buttons">
        {CATEGORY_LIST.map(category => (
          <button
            key={category}
            onClick={() => setSelectedCategory(category)}
            className={`category-button ${selectedCategory === category ? "active" : ""}`}
          >
            {category}
          </button>
        ))}
      </div>

      <div className="menu-list">
        {filteredItems.map(item => (
          <div key={item.id} className="menu-card" onClick={() => setSelectedItem(item)}>
            <img 
                //src={`http://localhost:8080/api/items/${item.id}/images`} 
                src={`http://localhost:3000/images/item-${item.id}.jpg`}
                alt={item.name} 
                onError={(e) => { e.target.src = "/images/coffee1.jpg"; }}/>
            <div className="menu-name">{item.name}</div>
            <div className="menu-price">₩{item.price.toLocaleString()}</div>
          </div>
        ))}
      </div>

      <Modal item={selectedItem} onClose={() => setSelectedItem(null)} onAddToCart={handleAddToCart} />
      {cartVisible && (
        <CartSideBar
          cartItems={cartItems}
          onUpdateQuantity={handleUpdateQuantity}
          onClose={() => setCartVisible(false)}
        />
      )}
    </div>
  );
}