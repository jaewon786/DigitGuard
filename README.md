# DigitGuard (디짓가드)

디지털 취약 계층(고령층)을 허위 광고, 피싱, 보이스피싱으로부터 보호하는 Android 앱입니다.

## 주요 기능

- **실시간 화면 감시** — AccessibilityService 기반, 허위 바이러스 경고 등 위험 키워드 실시간 감지
- **전체화면 경고 + TTS** — 위험 감지 시 큰 글씨 오버레이 경고 + 음성 안내
- **앱 설치 가디언** — 새 앱 설치 시 보호자에게 승인 요청 전송
- **링크 안전 검사** — 피싱/악성 URL 자동 탐지
- **보호자 대시보드** — 원격으로 피보호자 보호 상태 확인 및 관리
- **교육 콘텐츠** — 사기 예방 교육 (카드뉴스 형태)
- **SOS 긴급 연락** — 원터치 보호자 전화 연결

## 기술 스택

### Android
- Kotlin + Jetpack Compose
- MVVM + Clean Architecture
- Hilt (DI), Room (로컬 DB), Retrofit (네트워크)
- Firebase Auth / FCM
- WorkManager (백그라운드 패턴 동기화)
- DataStore (사용자 설정 영속화)

### Backend
- Node.js + Express
- PostgreSQL + 인메모리 이중 모드 (DAL 패턴)
- Firebase Admin SDK (FCM 푸시)
- Swagger UI (API 문서)

## 프로젝트 구조

```
digitGuard/
├── android/                # Android 앱 (Kotlin)
│   ├── app/src/main/
│   │   ├── java/.../       # 소스 코드
│   │   └── res/            # 리소스
│   └── app/src/test/       # 단위 테스트
├── backend/                # Express 서버
│   ├── src/
│   │   ├── controllers/    # API 컨트롤러 (8개)
│   │   ├── routes/         # 라우트 (8개)
│   │   ├── db/             # DAL + PostgreSQL + 인메모리
│   │   ├── services/       # Firebase, FCM 알림
│   │   └── middleware/     # 인증, Rate Limiting, 에러 처리
│   ├── test/               # API 테스트 (30개)
│   └── swagger.json        # OpenAPI 3.0 (23개 엔드포인트)
├── docs/                   # 출시 문서
│   ├── privacy-policy.md
│   ├── accessibility-declaration.md
│   └── play-store-listing.md
├── docker-compose.yml      # PostgreSQL + Backend 실행
└── .github/workflows/      # CI/CD
```

## 빠른 시작

### 백엔드 (개발 모드 — 인메모리)

```bash
cd backend
npm install
npm start
# http://localhost:3000 (랜딩 페이지)
# http://localhost:3000/api-docs (Swagger UI)
```

### 백엔드 (Docker — PostgreSQL)

```bash
docker compose up
# PostgreSQL + Express 서버 자동 실행
# 위험 패턴 시드 데이터 자동 삽입
```

### 테스트

```bash
cd backend
npm test          # 30개 API 테스트
```

### Android

1. Firebase 콘솔에서 프로젝트 생성
2. `android/app/google-services.json` 배치
3. Android Studio에서 프로젝트 열기
4. Run 실행

## API 엔드포인트 (23개)

| 모듈 | 엔드포인트 수 | 주요 기능 |
|------|-------------|-----------|
| 인증 | 3 | 회원가입, 로그인, 계정 삭제 |
| 보호자 | 5 | 연결 코드, 코드 수락, 피보호자 목록, 대시보드 |
| 위험 감지 | 4 | 텍스트 분석, 로그 업로드, 패턴 동기화, 이력 |
| 앱 관리 | 4 | 앱 검사, 설치 요청, 승인/거부, 화이트리스트 |
| URL 검사 | 1 | URL 안전성 검사 |
| 교육 | 2 | 콘텐츠 목록, 상세 |
| 디바이스 | 2 | FCM 토큰 등록/삭제 |
| 화이트리스트 | 3 | 조회, 추가, 삭제 |

## 라이선스

MIT
