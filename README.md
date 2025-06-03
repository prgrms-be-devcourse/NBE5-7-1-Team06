# ☕ 카페 주문 관리 시스템

작은 로컬 카페를 위한 주문/배송 시스템  

백엔드 데브코스 5기 7회차 1차 프로젝트

---

## 🙌 팀원/역할 소개

|                                           [임예성](https://github.com/sjsk3232)                                           |                                          [김광현](https://github.com/kwang2134)                                           |                                                        [안필온](https://github.com/KEEKE132)                                                        |                                          [임강현](https://github.com/LimKangHyun)                                           |                                                        [송희수](https://github.com/Shs0160)                                                         |
|:---------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/72946232?v=4" alt="임예성" width="150"> | <img src="https://avatars.githubusercontent.com/u/118968786?v=4" alt="김광현" width="150"> | <img src="https://avatars.githubusercontent.com/u/88488962?v=4" alt="안필온" width="150"> | <img src="https://avatars.githubusercontent.com/u/113881138?v=4" alt="임강현" width="150"> | <img src="https://avatars.githubusercontent.com/u/89959004?v=4" alt="송희수" width="150"> |
|                                         PM                                         |                                           TM                                          |                                                        TM                                                         |                                           TM                                          |                                                       TM                                                         |
| 주문/배송 메일 발송<br>배송 스케줄링<br>주소 검색/주문 유효성 검사 | 관리자 로그인 기능<br>관리자 로그인 페이지<br>시큐리티 설정 | 관리자 상품 페이지<br>관리자 상품 CRUD<br>파일 관리 | 주문 생성<br>관리자 주문 페이지 | 사용자 웹페이지(React)<br>상품 조회 추가 기능<br>주문 조회 / 상태 변경 |

---

## 📌 프로젝트 개요

이 프로젝트는 **회원가입 없이 이메일만으로 주문이 가능한** 커피 패키지 온라인 주문 관리 시스템입니다.  
고객은 웹 페이지에서 커피를 주문하고, 관리자는 주문을 조회하고 배송까지 관리할 수 있습니다.

- **오후 2시 기준**으로 주문을 구분하여 배송 처리
- 주문 및 배송 시작 시, 주문자의 이메일로 주문/배송 명세서 발송

### 📆 개발 기간

\`25.04.22. ~ \`25.04.30. (약 1주)

---

## 🛠 기술 스택

### 🔧 Back-End
<p>
  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/H2DB-09476B?style=for-the-badge&logo=h2database"/>
</p>

### 🎨 Front-End
<p>
  <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"/>
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=spring&logoColor=white"/>
</p>

### ⚙️ 협업 도구
<p>
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github"/>
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"/>
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion"/>
  <img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/>
</p>

---

## 🧩 시스템 아키텍처

![system architecture](https://github.com/user-attachments/assets/133afaf9-e0f0-43f0-9a45-b9604fff8a17)

---

## 📄 ERD (Entity Relationship Diagram)

![ERD](https://github.com/user-attachments/assets/131f29c1-e171-438e-9aaf-6d6d7bf9d60e)

---

## 🔁 Flow Chart

![Flow Chart](https://github.com/user-attachments/assets/bc291c5a-bd83-4619-9821-fae8ee85ff9e)

---

## ✅ 주요 기능

### 사용자 페이지
- 상품 목록 및 카테고리별 조회
- 상세 페이지 팝업 + 장바구니 추가
- 장바구니 수량 조정 및 구매 진행
- 주소 검색 및 주문 정보 유효성 검사

### 관리자 페이지
- 관리자 로그인 및 권한 인증 (Spring Security)
- 상품 CRUD 및 이미지 관리
- 주문 목록 확인, 검색, 페이징

### 주문 / 배송 시작 처리
- 주문 / 배송 시작 시 이메일 발송
- 일괄 배송 시작 스케줄링

---

## 🖥️ UI / 시연 영상

[<img src="https://img.shields.io/badge/Figma UI 링크-F24E1E?style=for-the-badge&logo=figma&logoColor=black"/>](https://www.figma.com/design/5JKdB5zqTuPCFTV6t5vbO5/Ipv6--1%EC%B0%A8-Project?node-id=0-1&t=RlI3AlFS0sV3UV8O-1)

https://github.com/user-attachments/assets/d6f07aad-a625-4992-a998-0da306027c5d

---

## 🛠️ 개발 컨벤션

<details>
<summary><b>1. Git 브랜치 전략 (GitLab Flow 기반)</b></summary> 
<br>

- `main`: 항상 배포 가능한 상태 유지
- `production`: 실제 배포 브랜치
- `feature`: 이슈 단위로 생성 (`feature: 이슈번호-기능명`)

> 예시: `feature: 12-add-menu`

#### 📌 작업 흐름

1. GitLab 이슈 생성  
2. 기능 브랜치 생성  
3. 기능 개발 및 PR 생성  
4. 팀 코드 리뷰 및 병합  
5. 정기적으로 `main → production` 병합 후 배포
<br>
</details>

<details>
<summary><b>2. Git 커밋 메시지 컨벤션</b></summary>
<br>
  
- **형식**: `태그: 제목`
- `:` 뒤에는 **공백 1칸**
- 태그는 **소문자**

| 태그 | 설명 |
|------|------|
| feat | 새로운 기능 추가 |
| fix | 버그 수정 |
| docs | 문서 수정 (README 등) |
| style | 코드 포맷팅 (세미콜론 누락, 공백 등) |
| refactor | 코드 리팩토링 (기능 변경 없음) |
| test | 테스트 코드 추가 및 수정 |
| chore | 빌드/패키지 설정 변경 등 |
  
> 예시: `feat: 커피 메뉴 등록 기능 추가`
<br>
</details>

<details>
<summary><b>3. 자바 코드 컨벤션</b></summary>  
<br>

| 항목 | 규칙 |
|------|------|
| 클래스명 | `PascalCase` |
| 변수/메서드명 | `camelCase` |
| 상수명 | `UPPER_SNAKE_CASE` |
| 한 줄 최대 글자 수 | 100자 |
| 줄임말 사용 | 지양 (관용적 줄임말만 허용) |
| 템플릿 파일명 | `kebab-case` |
</details>
