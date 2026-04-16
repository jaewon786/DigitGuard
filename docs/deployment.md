# DigitGuard 배포 가이드

## 1. 백엔드 배포

### Option A: Docker Compose (권장)

```bash
# 프로젝트 루트에서
docker compose up -d

# 확인
curl http://localhost:3000/health
```

PostgreSQL + Express 서버가 자동으로 실행됩니다.

### Option B: Railway / Render (무료 클라우드)

**Railway:**
1. https://railway.app 가입
2. New Project → Deploy from GitHub repo
3. backend 폴더를 Root Directory로 설정
4. 환경변수 추가:
   - `PORT=3000`
   - `NODE_ENV=production`
   - PostgreSQL 플러그인 추가 → DB 환경변수 자동 설정

**Render:**
1. https://render.com 가입
2. New → Web Service → Connect GitHub repo
3. Root Directory: `backend`
4. Build Command: `npm install`
5. Start Command: `node src/server.js`
6. PostgreSQL: New → PostgreSQL → 환경변수 연결

### Option C: AWS EC2

```bash
# EC2 인스턴스에서
git clone https://github.com/jaewon786/DigitGuard.git
cd DigitGuard
docker compose up -d
```

### 환경변수 (.env)

```
PORT=3000
NODE_ENV=production
DB_HOST=localhost
DB_PORT=5432
DB_NAME=digitguard
DB_USER=digitguard
DB_PASSWORD=<강력한_비밀번호>
FIREBASE_PROJECT_ID=<Firebase_프로젝트_ID>
FIREBASE_PRIVATE_KEY=<Firebase_서비스_계정_키>
FIREBASE_CLIENT_EMAIL=<Firebase_클라이언트_이메일>
GOOGLE_SAFE_BROWSING_API_KEY=<API_키>
```

## 2. Android 배포

### Debug APK (테스트용)

```bash
cd android
./gradlew assembleDevDebug
# 출력: app/build/outputs/apk/dev/debug/app-dev-debug.apk
```

### Release AAB (Play Store)

```bash
cd android
./gradlew bundleProdRelease
# 출력: app/build/outputs/bundle/prodRelease/app-prod-release.aab
```

### 서버 URL 변경

Android에서 프로덕션 서버를 가리키려면 `app/build.gradle.kts`의 release buildType에서 `BASE_URL`을 수정:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://your-server.com/api/v1/\"")
```

## 3. Firebase 설정

1. https://console.firebase.google.com → 프로젝트 생성
2. Android 앱 추가 → 패키지명: `com.digitguard.app`
3. `google-services.json` 다운로드 → `android/app/`에 배치
4. Authentication → 이메일/비밀번호 로그인 활성화
5. Cloud Messaging → 자동 활성화

### 서버에 Firebase Admin 설정

1. Firebase 콘솔 → 프로젝트 설정 → 서비스 계정
2. "새 비공개 키 생성" → JSON 다운로드
3. `.env`에 값 입력:
   - `FIREBASE_PROJECT_ID`
   - `FIREBASE_PRIVATE_KEY`
   - `FIREBASE_CLIENT_EMAIL`

## 4. 도메인 설정 (선택)

백엔드 서버에 커스텀 도메인을 연결하면 `BASE_URL`을 `https://api.digitguard.app/api/v1/`로 설정할 수 있습니다.
