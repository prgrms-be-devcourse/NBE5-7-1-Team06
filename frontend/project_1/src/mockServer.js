//import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import api from "./api/axios";

if (process.env.NODE_ENV === "development") {
  const mock = new MockAdapter(api, { delayResponse: 500 });

  const getRandomImage = () => `/images/item-${Math.floor(Math.random() * 5 + 1)}.jpg`;

  const dummyItems = [
    // BEAN
    {
      id: 1,
      name: "콜롬비아 수프리모",
      price: 12000,
      category: "BEAN",
      description: "산미와 단맛이 조화로운 원두",
      image: getRandomImage(),
    },
    {
      id: 2,
      name: "에티오피아 예가체프",
      price: 13000,
      category: "BEAN",
      description: "플로럴 향과 부드러운 바디감",
      image: getRandomImage(),
    },
    {
      id: 3,
      name: "케냐 AA",
      price: 12500,
      category: "BEAN",
      description: "풍부한 과일향과 진한 풍미",
      image: getRandomImage(),
    },
    {
      id: 4,
      name: "브라질 산토스",
      price: 11000,
      category: "BEAN",
      description: "고소한 견과류 향과 균형 잡힌 맛",
      image: getRandomImage(),
    },
    // TEA
    {
      id: 5,
      name: "캐모마일 허브티",
      price: 3700,
      category: "TEA",
      description: "은은한 향으로 편안함을 주는 허브티",
      image: getRandomImage(),
    },
    {
      id: 6,
      name: "얼그레이 블랙티",
      price: 4000,
      category: "TEA",
      description: "베르가못 향이 매력적인 홍차",
      image: getRandomImage(),
    },
    {
      id: 7,
      name: "페퍼민트 티",
      price: 3900,
      category: "TEA",
      description: "상쾌한 민트향으로 리프레시",
      image: getRandomImage(),
    },
    {
      id: 8,
      name: "자스민 그린티",
      price: 4100,
      category: "TEA",
      description: "자스민 향과 녹차의 깔끔한 조화",
      image: getRandomImage(),
    },
    // GOODS
    {
      id: 9,
      name: "Grids 텀블러",
      price: 15000,
      category: "GOODS",
      description: "보온보냉이 뛰어난 공식 텀블러",
      image: getRandomImage(),
    },
    {
      id: 10,
      name: "Grids 머그컵",
      price: 12000,
      category: "GOODS",
      description: "심플한 로고 머그컵",
      image: getRandomImage(),
    },
    {
      id: 11,
      name: "Grids 키링",
      price: 6000,
      category: "GOODS",
      description: "로고가 각인된 아크릴 키링",
      image: getRandomImage(),
    },
    {
      id: 12,
      name: "Grids 노트",
      price: 8000,
      category: "GOODS",
      description: "커피 감성 가득한 A5 사이즈 노트",
      image: getRandomImage(),
    },
    // OTHER
    {
      id: 13,
      name: "기프트 카드",
      price: 5000,
      category: "OTHER",
      description: "소중한 사람에게 커피를 선물해보세요",
      image: getRandomImage(),
    },
    {
      id: 14,
      name: "에코백",
      price: 18000,
      category: "OTHER",
      description: "Grids 로고가 박힌 감성 에코백",
      image: getRandomImage(),
    },
    {
      id: 15,
      name: "스티커 팩",
      price: 3000,
      category: "OTHER",
      description: "다양한 Grids 감성 스티커",
      image: getRandomImage(),
    },
    {
      id: 16,
      name: "랜덤 굿즈 박스",
      price: 20000,
      category: "OTHER",
      description: "Grids 굿즈가 랜덤으로 들어있는 서프라이즈 박스",
      image: getRandomImage(),
    },
  ];

  mock.onGet("/items/all").reply(200, dummyItems);

  mock.onGet(new RegExp("/items/\\d+/images")).reply(config => {
    const itemId = config.url.match(/\/items\/(\d+)\/images/)[1];
    return [
      200,
      {}, 
      { headers: { "Content-Type": "image/jpeg" }, url: `/images/item-${itemId % 5 + 1}.jpg`},
    ];
  });

  console.log("[MockServer] Mock API 실행중");
}