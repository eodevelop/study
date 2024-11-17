## 들어가며
- 최근 리액트로 프로젝트 초기 구성을 자주하는데 해당 내용을 정리하기 위함
- vite를 통해서 react + typescript + scss 환경을 구축할 예정
- 추가로 화면의 이동을 위한 라우팅을 react-router-dom 으로 구성
- ESLint, prettier를 통한 코드 품질 관리
- node, npm, vite 등은 설치 되어 있다는 가정하에 작업 진행

## 시작
### 1. 프로젝트 생성
- 생성할 프로젝트 명으로 해당 폴더로 이동
``` bash
# 프로젝트 디렉토리 생성 및 이동
mkdir portfolio
cd portfolio

# Vite를 사용하여 React + TypeScript 프로젝트 생성
npm create vite@latest . -- --template react-ts
```
- `-- --template react-ts`는 템플릿으로 React와 TypeScript를 사용하겠다는 의미
- 프로젝트 디렉토리로 이동하여 패키지 설치
``` bash
npm install
```

### 2. scss 설정하기
- scss 패키지 설치
``` bash
npm install sass
```

### 3. 폴더 구조 설정
- 현업에서 자주 사용하는 형태로 src 폴더 구성
``` bash
cd src
mkdir components pages routes styles assets
```
- 위 명령은 wsl 환경에서 사용하는 것을 권장 윈도우에서는 각각 폴더를 생성하는 방식으로 진행

### 4. 홈페이지 화면 구현하기
- src/pages/HomePage.tsx 파일 생성
``` typescript
// src/pages/HomePage.tsx
import React from 'react';
import './HomePage.scss';
import Header from '../components/Header';

const HomePage: React.FC = () => {
  return (
    <div className="home-page">
      <Header />
      <h1>환영합니다!</h1>
    </div>
  );
};

export default HomePage;
```
- src/pages/HomePage.scss 파일 생성
``` scss
/* src/pages/HomePage.scss */

.home-page {
  text-align: center;
  padding: 2rem;

  h1 {
    font-size: 2.5rem;
    color: #333;
  }

  p {
    font-size: 1.2rem;
    color: #666;
  }
}
```

### 5. 컴포넌트 생성하기
- src/components/Header.tsx 파일 생성
``` typescript
// src/components/Header.tsx
import React from 'react';
import { Link } from 'react-router-dom';
import './Header.scss';

const Header: React.FC = () => {
  return (
    <header className="header">
      <nav>
        <Link to="/">홈</Link>
        {/* 다른 링크 추가 가능 */}
      </nav>
    </header>
  );
};

export default Header;
```
- src/components/Header.scss 파일 생헝
``` scss
/* src/components/Header.scss */

.header {
  background-color: #f8f8f8;
  padding: 1rem;

  nav {
    display: flex;
    justify-content: center;

    a {
      margin: 0 1rem;
      color: #333;
      font-weight: bold;
    }
  }
}
```

### 6. 라우팅 설정하기
- react router를 사용하기 위한 패키지를 설치
``` bash
npm install react-router-dom
```
- src/routes/AppRouter.tsx 파일 생성
``` typescript
// src/routes/AppRouter.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from '../pages/HomePage';

const AppRouter: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        {/* 다른 라우트를 추가할 수 있습니다 */}
      </Routes>
    </Router>
  );
};

export default AppRouter;
```

### 7. 전역 스타일 설정하기
- src/styles/global.scss 파일 생성
``` scss
/* src/styles/global.scss */

body {
  margin: 0;
  padding: 0;
  font-family: 'Arial, sans-serif';
}

a {
  text-decoration: none;
  color: inherit;
}
```
- 전역 스타일 적용
``` typescript
// src/main.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './styles/global.scss';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

8. App.tsx 파일 수정
``` typescript
// src/App.tsx
import React from 'react';
import AppRouter from './routes/AppRouter';

const App: React.FC = () => {
  return <AppRouter />;
};

export default App;
```

### 9. ESLint, Prettier 설정하기
- 필요 패키지 설치
``` bash
npm install --save-dev eslint prettier eslint-plugin-react eslint-plugin-react-hooks @typescript-eslint/parser @typescript-eslint/eslint-plugin eslint-config-prettier eslint-plugin-prettier
```
- .eslintrc.js 파일 생성
``` javascript
// .eslintrc.js
module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
    },
  },
  settings: {
    react: {
      version: 'detect',
    },
  },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended', // React 규칙
    'plugin:@typescript-eslint/recommended', // TypeScript 규칙
    'plugin:prettier/recommended', // Prettier 규칙
  ],
  plugins: ['react', '@typescript-eslint', 'prettier'],
  rules: {
    // 추가 규칙 설정 가능
    'prettier/prettier': 'error',
  },
};
```
- .prettierrc 파일 생성
``` json
// .prettierrc
{
  "singleQuote": true,
  "trailingComma": "all",
  "printWidth": 80,
  "tabWidth": 2,
  "semi": true
}
```
- package.json에 스크립트 추가
``` json
// package.json
{
  "scripts": {
    "lint": "eslint --ext .js,.jsx,.ts,.tsx src",
    "lint:fix": "eslint --fix --ext .js,.jsx,.ts,.tsx src",
    // 나머지 스크립트
  }
}
```

### 10. 프로젝트 실행 및 확인