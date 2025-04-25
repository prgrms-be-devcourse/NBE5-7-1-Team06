import React, { useState, useEffect } from "react";
import api from "../api/axios";
import Modal from "../components/Modal.jsx";
import CartSideBar from "../components/CartSideBar.jsx";
import "./MainPage.css";

export default function MainPage() {
  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [selectedItem, setSelectedItem] = useState(null);
  const [cartItems, setCartItems] = useState([]);
  const [cartVisible, setCartVisible] = useState(false);


  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await api.get("/items"); 
        setItems(response.data);
        extractCategories(response.data);
      } catch (error) {
        console.error("메뉴 데이터 불러오기 실패:", error);

        const dummyItems = [
            { id: 1, name: "Americano", price: 3000, category: "Coffee", image: "/images/coffee1.jpg", description: "진한 에스프레소와 시원한 물의 조화" },
            { id: 2, name: "Latte", price: 3500, category: "Coffee", image: "/images/coffee2.png", description: "우유와 커피의 부드러운 만남" },
            { id: 3, name: "Mocha", price: 4000, category: "Coffee", image: "/images/coffee3.png", description: "달콤한 초코와 커피의 조화" },
            { id: 4, name: "Green Tea", price: 3800, category: "Non-Coffee", image: "/images/green-tea.jpg", description: "향긋한 녹차" },
            { id: 5, name: "Lemonade", price: 3200, category: "Non-Coffee", image: "/images/lemonade.jpg", description: "상큼한 레몬향이 가득" }
          ];
          setItems(dummyItems);
          extractCategories(dummyItems);
        }
      };
  
      const extractCategories = (data) => {
        const uniqueCategories = [...new Set(data.map(item => item.category))];
        setCategories(uniqueCategories);
        setSelectedCategory(uniqueCategories[0] || "");
        
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
        {categories.map(category => (
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
                src={`http://localhost:8080/items/${item.id}/images`} 
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