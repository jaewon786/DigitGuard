# DigitGuard — 디지털 취약 계층 보호 앱 상세 개발 계획서

> **프로젝트명:** DigitGuard (디짓가드)
> **패키지명:** com.digitguard.app
> **플랫폼:** Android (최소 SDK 26 / Android 8.0 이상)
> **작성일:** 2026-04-16
> **프로젝트 경로:** `DigitGuard/`

---

## 1. 프로젝트 배경 및 문제 정의

### 1.1 문제 상황

플레이스토어나 웹 브라우저 사용 중 "핸드폰이 바이러스에 걸렸습니다", "악성코드에 노출되었습니다", "개인정보를 보호하려면 앱을 설치하세요"와 같은 허위 광고가 빈번하게 노출됩니다. 디지털 기기에 익숙하지 않은 고령층은 이러한 광고를 실제 시스템 경고로 오인하여 불필요한 앱을 설치하거나, 개인정보를 유출하거나, 금전적 피해를 입는 경우가 발생합니다.

### 1.2 대상 사용자

- **주 사용자:** 60세 이상 고령층 (스마트폰 사용에 익숙하지 않은 분들)
- **보조 사용자:** 가족 구성원 (원격으로 보호 상태를 확인하고 관리하는 역할)

### 1.3 핵심 목표

1. 허위 광고 및 의심스러운 팝업을 실시간으로 감지하고 경고한다.
2. 위험한 앱 설치를 사전에 차단하거나 보호자에게 알린다.
3. 고령층이 직관적으로 사용할 수 있는 극도로 단순한 UI를 제공한다.
4. 보호자(가족)가 원격으로 보호 상태를 모니터링할 수 있게 한다.

---

## 2. 핵심 기능 정의

### 2.1 실시간 화면 감시 및 경고 (핵심)

| 항목 | 상세 |
|------|------|
| **기능** | 화면에 표시되는 텍스트를 실시간으로 분석하여 허위 광고 패턴 감지 |
| **기술** | Android AccessibilityService를 활용한 화면 텍스트 모니터링 |
| **동작** | "바이러스 감지", "악성코드 노출", "지금 설치하세요" 등의 위험 키워드 패턴 매칭 |
| **경고 방식** | 전체 화면 오버레이로 큰 글씨의 경고 메시지 표시 ("이것은 거짓 광고입니다. 무시하세요.") |
| **추가 동작** | 경고 발생 시 보호자에게 자동 푸시 알림 전송 |

**위험 키워드 패턴 예시:**
```
- "바이러스가 발견되었습니다"
- "악성코드에 노출"
- "핸드폰이 감염"
- "지금 치료하세요"
- "개인정보가 유출"
- "보안 앱을 설치"
- "긴급 업데이트 필요"
- "배터리가 손상"
- "기기가 해킹"
```

### 2.2 앱 설치 감시 (App Install Guardian)

| 항목 | 상세 |
|------|------|
| **기능** | 새로운 앱 설치 시도를 감지하고, 의심스러운 앱이면 경고 |
| **기술** | PackageInstaller 브로드캐스트 리시버 + Google Safe Browsing API |
| **판단 기준** | 앱 평점, 다운로드 수, 개발자 신뢰도, 요청 권한 수준 |
| **동작** | 위험도 높은 앱 설치 시 보호자에게 승인 요청 전송 |

### 2.3 링크 안전 검사

| 항목 | 상세 |
|------|------|
| **기능** | 사용자가 클릭하는 URL의 안전성을 사전 검증 |
| **기술** | Google Safe Browsing API, VirusTotal API 연동 |
| **동작** | 피싱/멀웨어 사이트 접근 시 차단 화면 표시 |

### 2.4 보호자 연동 시스템

| 항목 | 상세 |
|------|------|
| **기능** | 가족이 원격으로 보호 상태 확인 및 관리 |
| **주요 기능** | 위험 감지 알림 수신, 앱 설치 승인/거부, 보호 상태 대시보드, 허용 앱 화이트리스트 관리 |
| **기술** | Firebase Cloud Messaging (FCM) + REST API |

### 2.5 교육 콘텐츠

| 항목 | 상세 |
|------|------|
| **기능** | 일반적인 사기 수법을 그림과 함께 쉽게 설명 |
| **형식** | 카드 뉴스 형태, 큰 글씨, 삽화 중심 |
| **주제** | 허위 광고 구별법, 의심 전화 대처법, 안전한 앱 사용법 등 |

### 2.6 긴급 연락 (SOS)

| 항목 | 상세 |
|------|------|
| **기능** | 홈 화면 위젯 또는 앱 내 큰 버튼으로 즉시 보호자에게 전화 |
| **동작** | 한 번 터치로 등록된 가족에게 자동 전화 연결 |

---

## 3. 기술 스택

### 3.1 클라이언트 (Android)

| 구분 | 기술 | 선정 이유 |
|------|------|-----------|
| **언어** | Kotlin | Android 공식 권장 언어, null 안전성 |
| **최소 SDK** | API 26 (Android 8.0) | 고령층 사용 기기 호환성 확보 |
| **UI 프레임워크** | Jetpack Compose | 선언적 UI로 빠른 개발, 접근성 지원 우수 |
| **아키텍처** | MVVM + Clean Architecture | 유지보수성과 테스트 용이성 |
| **DI** | Hilt | Android 공식 DI 프레임워크 |
| **비동기 처리** | Kotlin Coroutines + Flow | 반응형 데이터 스트림 처리 |
| **로컬 DB** | Room | SQLite 추상화, 타입 안전성 |
| **네트워크** | Retrofit2 + OkHttp | 안정적 HTTP 클라이언트 |
| **이미지** | Coil | Kotlin 최적화 이미지 로딩 |
| **푸시 알림** | Firebase Cloud Messaging | 보호자 알림 전달 |

### 3.2 백엔드 서버

| 구분 | 기술 | 선정 이유 |
|------|------|-----------|
| **런타임** | Node.js (Express) 또는 Spring Boot | 빠른 개발 vs 엔터프라이즈 안정성 |
| **DB** | PostgreSQL | 관계형 데이터 관리, 안정성 |
| **캐시** | Redis | 위험 키워드 패턴 캐싱, 세션 관리 |
| **인증** | Firebase Auth | 간편한 소셜 로그인 + 이메일 인증 |
| **클라우드** | AWS 또는 GCP | 확장성, 한국 리전 지원 |
| **API 문서** | Swagger/OpenAPI 3.0 | API 명세 자동화 |

### 3.3 외부 API

| API | 용도 |
|-----|------|
| Google Safe Browsing API | URL 안전성 검사 |
| Google Play Developer API | 앱 정보 조회 (평점, 리뷰 등) |
| VirusTotal API | 악성 URL/파일 검사 (보조) |
| Firebase Cloud Messaging | 보호자 푸시 알림 |

---

## 4. 시스템 아키텍처

### 4.1 전체 구조

```
┌─────────────────────────────────────────────────┐
│                  Android 앱 (주 사용자)              │
│  ┌───────────┐  ┌──────────┐  ┌───────────────┐  │
│  │Accessibility│  │   App    │  │   Link       │  │
│  │  Service   │  │ Install  │  │   Safety     │  │
│  │ (화면감시) │  │ Guardian │  │   Checker    │  │
│  └─────┬─────┘  └────┬─────┘  └──────┬────────┘  │
│        └──────────────┼───────────────┘            │
│                  ┌────▼─────┐                      │
│                  │ 위험 판단 │                      │
│                  │  Engine   │                      │
│                  └────┬─────┘                      │
│                       │                            │
└───────────────────────┼────────────────────────────┘
                        │ HTTPS (REST API)
                  ┌─────▼──────┐
                  │  Backend   │
                  │  Server    │
                  │ ┌────────┐ │
                  │ │ 패턴 DB │ │
                  │ │ 사용자DB│ │
                  │ │ 로그 DB │ │
                  │ └────────┘ │
                  └─────┬──────┘
                        │ FCM
          ┌─────────────▼──────────────┐
          │  보호자 앱 / 웹 대시보드     │
          │  (알림 수신, 원격 관리)       │
          └────────────────────────────┘
```

### 4.2 위험 판단 엔진 (로컬 + 서버 하이브리드)

```
화면 텍스트 수집 (AccessibilityService)
        │
        ▼
  ┌─────────────────┐
  │ 1차: 로컬 패턴   │  ← 오프라인에서도 동작 (내장 키워드 DB)
  │    매칭          │
  └────────┬────────┘
           │ 의심 텍스트 발견
           ▼
  ┌─────────────────┐
  │ 2차: 서버 정밀   │  ← 최신 패턴 DB + ML 모델 활용
  │    분석          │
  └────────┬────────┘
           │ 위험도 점수 산출
           ▼
  ┌─────────────────┐
  │ 위험도별 대응    │
  │ 높음: 즉시 경고  │
  │ 중간: 주의 안내  │
  │ 낮음: 로그 기록  │
  └─────────────────┘
```

---

## 5. 데이터베이스 설계

### 5.1 서버 DB (PostgreSQL)

**users 테이블 — 사용자 정보**
```sql
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firebase_uid    VARCHAR(128) UNIQUE NOT NULL,
    name            VARCHAR(50) NOT NULL,
    phone           VARCHAR(20),
    role            VARCHAR(10) NOT NULL CHECK (role IN ('protected', 'guardian')),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

**guardian_links 테이블 — 보호자-피보호자 연결**
```sql
CREATE TABLE guardian_links (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    guardian_id     UUID REFERENCES users(id) ON DELETE CASCADE,
    protected_id    UUID REFERENCES users(id) ON DELETE CASCADE,
    status          VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'active', 'revoked')),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(guardian_id, protected_id)
);
```

**threat_logs 테이블 — 위험 감지 기록**
```sql
CREATE TABLE threat_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users(id) ON DELETE CASCADE,
    threat_type     VARCHAR(30) NOT NULL,  -- 'fake_ad', 'suspicious_app', 'phishing_url'
    threat_level    VARCHAR(10) NOT NULL,  -- 'high', 'medium', 'low'
    detected_text   TEXT,
    source_package  VARCHAR(200),
    action_taken    VARCHAR(30),  -- 'blocked', 'warned', 'logged'
    created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

**threat_patterns 테이블 — 위험 키워드 패턴**
```sql
CREATE TABLE threat_patterns (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pattern         TEXT NOT NULL,
    pattern_type    VARCHAR(20) NOT NULL,  -- 'keyword', 'regex', 'url_pattern'
    threat_level    VARCHAR(10) NOT NULL,
    category        VARCHAR(30) NOT NULL,  -- 'fake_virus', 'fake_security', 'phishing'
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

**app_install_requests 테이블 — 앱 설치 승인 요청**
```sql
CREATE TABLE app_install_requests (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    protected_id    UUID REFERENCES users(id),
    package_name    VARCHAR(200) NOT NULL,
    app_name        VARCHAR(200),
    risk_score      INTEGER CHECK (risk_score BETWEEN 0 AND 100),
    guardian_decision VARCHAR(10) CHECK (guardian_decision IN ('approved', 'rejected', 'pending')),
    decided_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

### 5.2 로컬 DB (Room)

```kotlin
// 로컬 위험 패턴 캐시
@Entity(tableName = "local_threat_patterns")
data class LocalThreatPattern(
    @PrimaryKey val id: String,
    val pattern: String,
    val patternType: String,    // keyword, regex
    val threatLevel: String,    // high, medium, low
    val category: String,
    val lastSyncedAt: Long
)

// 위험 감지 로그 (오프라인 캐시)
@Entity(tableName = "local_threat_logs")
data class LocalThreatLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threatType: String,
    val detectedText: String,
    val sourcePackage: String?,
    val actionTaken: String,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// 보호자 정보
@Entity(tableName = "guardians")
data class Guardian(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val fcmToken: String?
)
```

---

## 6. API 명세

### 6.1 인증

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/auth/register` | 회원가입 (Firebase ID Token 검증) |
| POST | `/api/v1/auth/login` | 로그인 |
| DELETE | `/api/v1/auth/account` | 계정 삭제 |

### 6.2 보호자 연동

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/guardian/link` | 보호자-피보호자 연결 요청 |
| PUT | `/api/v1/guardian/link/{id}/accept` | 연결 요청 수락 |
| GET | `/api/v1/guardian/protected-users` | 피보호자 목록 조회 |
| GET | `/api/v1/guardian/dashboard/{userId}` | 피보호자 보호 현황 대시보드 |

### 6.3 위험 감지

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/threats/analyze` | 텍스트 위험도 분석 요청 |
| POST | `/api/v1/threats/log` | 위험 감지 로그 기록 (배치 업로드) |
| GET | `/api/v1/threats/patterns/sync` | 최신 위험 패턴 동기화 |
| GET | `/api/v1/threats/history/{userId}` | 위험 감지 이력 조회 |

### 6.4 앱 설치 관리

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/apps/check` | 앱 안전성 검사 |
| POST | `/api/v1/apps/install-request` | 설치 승인 요청 (보호자에게) |
| PUT | `/api/v1/apps/install-request/{id}` | 설치 승인/거부 응답 |
| GET | `/api/v1/apps/whitelist/{userId}` | 허용 앱 목록 조회 |

### 6.5 URL 검사

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/urls/check` | URL 안전성 검사 |

### 6.6 교육 콘텐츠

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/education/contents` | 교육 콘텐츠 목록 |
| GET | `/api/v1/education/contents/{id}` | 콘텐츠 상세 조회 |

**요청/응답 예시 — 텍스트 위험도 분석:**

```json
// POST /api/v1/threats/analyze
// Request
{
  "text": "귀하의 핸드폰이 바이러스 4개에 감염되었습니다. 지금 치료하세요.",
  "sourcePackage": "com.android.chrome",
  "deviceId": "abc123"
}

// Response
{
  "threatLevel": "high",
  "riskScore": 95,
  "category": "fake_virus",
  "matchedPatterns": ["바이러스.*감염", "지금.*치료"],
  "recommendation": "이것은 거짓 광고입니다. 무시하고 뒤로가기를 눌러주세요.",
  "shouldNotifyGuardian": true
}
```

---

## 7. 화면 설계

### 7.1 설계 원칙

- **큰 글씨:** 기본 폰트 크기 20sp 이상, 제목은 28sp 이상
- **높은 대비:** 배경은 흰색, 텍스트는 진한 검정, 버튼은 원색 계열
- **최소 터치 영역:** 모든 터치 요소는 최소 56dp × 56dp
- **단순한 네비게이션:** 최대 2depth, 하단 탭 3개 이하
- **음성 안내:** 주요 경고는 TTS(Text-to-Speech)로 음성 동시 출력

### 7.2 화면 구성

```
[초기 설정]
  ├── 1. 환영 화면 (큰 글씨 안내)
  ├── 2. 보호자 연결 (QR코드 또는 전화번호 입력)
  ├── 3. 권한 설정 안내 (접근성 서비스, 알림 등)
  └── 4. 설정 완료

[메인 - 하단 탭 3개]
  ├── 🏠 홈
  │     ├── 보호 상태 표시 (큰 방패 아이콘: "안전합니다" / "주의가 필요합니다")
  │     ├── 최근 차단 내역 (간단 카드)
  │     └── SOS 긴급 연락 버튼 (화면 하단 고정, 빨간 큰 버튼)
  │
  ├── 📚 배우기
  │     ├── 사기 유형별 교육 카드
  │     └── 오늘의 안전 팁
  │
  └── ⚙️ 설정
        ├── 보호자 관리
        ├── 보호 수준 조절 (단순 3단계: 높음/보통/낮음)
        ├── 알림 소리 설정
        └── 글씨 크기 조절

[경고 오버레이 — 위험 감지 시 전체 화면]
  ┌─────────────────────────────┐
  │                             │
  │    ⚠️ 주의하세요!           │
  │                             │
  │  이것은 거짓 광고입니다.    │
  │  절대 설치하지 마세요.      │
  │                             │
  │  [무시하고 돌아가기]        │  ← 큰 녹색 버튼
  │                             │
  │  [보호자에게 전화하기]      │  ← 큰 파란 버튼
  │                             │
  └─────────────────────────────┘
  * TTS 음성 동시 출력: "주의하세요. 이것은 거짓 광고입니다."
```

### 7.3 보호자용 화면 (동일 앱 내 모드 전환)

```
[보호자 대시보드]
  ├── 피보호자 상태 요약 (안전/주의 표시)
  ├── 최근 위험 감지 이력 (시간순 리스트)
  ├── 앱 설치 승인 대기 목록
  ├── 허용 앱 관리 (화이트리스트)
  └── 피보호자 설정 원격 관리
```

---

## 8. 핵심 기술 구현 상세

### 8.1 AccessibilityService 구현

```kotlin
// com.digitguard.service.ScreenMonitorService
class ScreenMonitorService : AccessibilityService() {

    private lateinit var threatEngine: ThreatDetectionEngine

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        // 화면 텍스트 변경 시 감지
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            val rootNode = rootInActiveWindow ?: return
            val screenText = extractAllText(rootNode)
            val sourcePackage = event.packageName?.toString() ?: ""

            // 위험 분석
            threatEngine.analyze(screenText, sourcePackage) { result ->
                when (result.threatLevel) {
                    ThreatLevel.HIGH -> showFullScreenWarning(result)
                    ThreatLevel.MEDIUM -> showNotificationWarning(result)
                    ThreatLevel.LOW -> logThreat(result)
                }
            }
        }
    }

    private fun extractAllText(node: AccessibilityNodeInfo): String {
        val builder = StringBuilder()
        if (node.text != null) builder.append(node.text).append(" ")
        if (node.contentDescription != null) builder.append(node.contentDescription).append(" ")
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { builder.append(extractAllText(it)) }
        }
        return builder.toString()
    }

    override fun onInterrupt() { /* 서비스 중단 처리 */ }
}
```

### 8.2 위험 판단 엔진

```kotlin
// com.digitguard.service.ThreatDetectionEngine
class ThreatDetectionEngine(
    private val localPatternDao: ThreatPatternDao,
    private val remoteAnalysisApi: ThreatAnalysisApi
) {
    // 1차: 로컬 패턴 매칭 (오프라인 동작)
    suspend fun analyzeLocally(text: String): ThreatResult {
        val patterns = localPatternDao.getActivePatterns()
        var maxThreatLevel = ThreatLevel.NONE
        val matched = mutableListOf<String>()

        for (pattern in patterns) {
            val regex = Regex(pattern.pattern, RegexOption.IGNORE_CASE)
            if (regex.containsMatchIn(text)) {
                matched.add(pattern.pattern)
                val level = ThreatLevel.valueOf(pattern.threatLevel.uppercase())
                if (level > maxThreatLevel) maxThreatLevel = level
            }
        }

        return ThreatResult(maxThreatLevel, matched)
    }

    // 2차: 서버 정밀 분석 (온라인)
    suspend fun analyzeRemotely(text: String, sourcePackage: String): ThreatResult {
        return try {
            remoteAnalysisApi.analyze(AnalyzeRequest(text, sourcePackage))
        } catch (e: Exception) {
            // 네트워크 오류 시 로컬 결과만 사용
            analyzeLocally(text)
        }
    }
}
```

### 8.3 앱 설치 감지

```kotlin
// com.digitguard.service.AppInstallReceiver
class AppInstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart ?: return
            // 서버에 앱 안전성 검사 요청
            // 위험 앱이면 보호자에게 알림 전송
        }
    }
}
```

---

## 9. 보안 고려사항

### 9.1 데이터 보호

- 서버-클라이언트 간 모든 통신은 HTTPS(TLS 1.3) 적용
- 사용자 민감 데이터(전화번호 등)는 AES-256으로 암호화하여 저장
- Firebase Auth 토큰 기반 인증, JWT Access Token + Refresh Token 이중 구조

### 9.2 개인정보 처리

- 화면 텍스트는 로컬에서 1차 분석 후 키워드 매칭 결과만 서버로 전송 (원문 전송 최소화)
- 개인정보보호법 준수: 수집 항목, 목적, 보유 기간 명시
- 사용자 동의 기반 데이터 수집, 철회 시 즉시 삭제

### 9.3 접근성 서비스 악용 방지

- Google Play 정책 준수: 접근성 서비스 사용 목적을 명확히 선언
- 수집하는 데이터 범위를 보호 목적으로 한정
- 2026년 Google의 강화된 접근성 API 정책에 맞춰 핵심 목적 명시 필수

---

## 10. 개발 일정 (16주)

### Phase 1: 기반 구축 (1~4주)

| 주차 | 작업 | 산출물 |
|------|------|--------|
| 1주 | 프로젝트 셋업, CI/CD 구성, 아키텍처 스캐폴딩 | 프로젝트 저장소, 빌드 파이프라인 |
| 2주 | 서버 인프라 구축, DB 스키마 생성, 인증 API | 서버 배포 환경, Auth API |
| 3주 | Android 앱 기본 구조, 네비게이션, 디자인 시스템 | 앱 스캐폴딩, UI 컴포넌트 라이브러리 |
| 4주 | 사용자 등록/로그인, 보호자 연결 기능 | 인증 + 페어링 기능 완성 |

### Phase 2: 핵심 기능 (5~9주)

| 주차 | 작업 | 산출물 |
|------|------|--------|
| 5주 | AccessibilityService 기반 화면 감시 구현 | 화면 텍스트 수집 모듈 |
| 6주 | 위험 판단 엔진 (로컬 패턴 매칭) | 로컬 분석 엔진 |
| 7주 | 서버 분석 API + 패턴 DB 구축 | 서버 분석 파이프라인 |
| 8주 | 경고 오버레이 UI + TTS 음성 안내 | 경고 시스템 완성 |
| 9주 | 앱 설치 감시 + 보호자 승인 플로우 | 앱 가디언 기능 |

### Phase 3: 부가 기능 (10~12주)

| 주차 | 작업 | 산출물 |
|------|------|--------|
| 10주 | 링크 안전 검사 (Safe Browsing 연동) | URL 검사 기능 |
| 11주 | 보호자 대시보드 + 원격 관리 | 보호자 기능 완성 |
| 12주 | 교육 콘텐츠 + SOS 긴급 연락 | 부가 기능 완성 |

### Phase 4: 품질 관리 및 출시 (13~16주)

| 주차 | 작업 | 산출물 |
|------|------|--------|
| 13주 | 고령층 대상 사용성 테스트 (5명 이상) | 사용성 테스트 보고서 |
| 14주 | 피드백 반영 및 UI/UX 개선 | 개선된 앱 |
| 15주 | 성능 최적화, 배터리 소모 최적화, 보안 감사 | 최적화 완료 |
| 16주 | 플레이스토어 심사 준비 및 출시 | 앱 출시 |

---

## 11. 주요 리스크 및 대응 방안

| 리스크 | 영향 | 대응 방안 |
|--------|------|-----------|
| Google Play 접근성 서비스 정책 변경 | 핵심 기능 불가 | 접근성 서비스 목적을 명확히 문서화, 대안으로 Notification Listener + Usage Stats 활용 |
| 배터리 과다 소모 | 사용자 불만 | 이벤트 스로틀링, 화면 꺼짐 시 모니터링 중단, WorkManager 기반 배치 처리 |
| 정상 광고 오탐지 (False Positive) | 신뢰도 하락 | 화이트리스트 관리, 사용자 피드백 반영, 점진적 패턴 정교화 |
| 고령층 초기 설정 어려움 | 이탈률 증가 | 보호자가 대신 설정 가능한 원격 설정 플로우, QR코드 기반 간편 페어링 |
| 개인정보보호법 규제 | 법적 리스크 | 최소 수집 원칙, 로컬 우선 처리, 개인정보 처리방침 법률 자문 |

---

## 12. 성공 지표 (KPI)

| 지표 | 목표값 | 측정 방법 |
|------|--------|-----------|
| 허위 광고 탐지율 | 90% 이상 | 테스트 셋 기반 정확도 측정 |
| 오탐지율 (False Positive) | 5% 이하 | 사용자 피드백 + 자동 로깅 |
| 경고 응답 시간 | 2초 이내 | 위험 감지~경고 표시까지 시간 |
| 배터리 소모 증가량 | 5% 이하 | Battery Historian 분석 |
| 고령층 사용성 점수 | SUS 70점 이상 | System Usability Scale 설문 |
| 보호자 알림 전달 성공률 | 99% 이상 | FCM 전달 로그 분석 |

---

## 13. 향후 확장 계획

1. **AI 기반 분석 강화:** 키워드 매칭에서 NLP 기반 문맥 분석으로 발전 (on-device ML 모델)
2. **보이스피싱 감지:** 통화 중 의심 키워드 탐지 및 경고
3. **스미싱 문자 필터링:** 의심 문자 메시지 자동 분류 및 경고
4. **iOS 버전 출시:** Kotlin Multiplatform 또는 Flutter로 크로스 플랫폼 확장
5. **지자체/복지관 연계:** 디지털 교육 프로그램과 연계한 B2G 서비스 모델
6. **커뮤니티 리포팅:** 사용자들이 새로운 사기 패턴을 신고하면 전체 DB에 반영하는 크라우드소싱 시스템

---

*본 문서는 2026년 4월 16일 기준으로 작성되었으며, 개발 진행 중 기술 환경 변화에 따라 수정될 수 있습니다.*