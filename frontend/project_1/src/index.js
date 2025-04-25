import React from 'react';
import ReactDOM from 'react-dom/client';

import App from './App.jsx';

//모킹 API 서버 사용
//if (process.env.NODE_ENV === "development") {
//  require("./mockServer.js");
//}


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

