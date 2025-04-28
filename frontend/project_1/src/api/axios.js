import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api', // 나중에 주소 확정되면 변경
  //baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default instance;