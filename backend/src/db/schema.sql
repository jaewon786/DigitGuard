-- DigitGuard 데이터베이스 스키마

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firebase_uid    VARCHAR(128) UNIQUE NOT NULL,
    name            VARCHAR(50) NOT NULL,
    phone           VARCHAR(20),
    role            VARCHAR(10) NOT NULL CHECK (role IN ('protected', 'guardian')),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE guardian_links (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    guardian_id     UUID REFERENCES users(id) ON DELETE CASCADE,
    protected_id    UUID REFERENCES users(id) ON DELETE CASCADE,
    status          VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'active', 'revoked')),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(guardian_id, protected_id)
);

CREATE TABLE threat_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users(id) ON DELETE CASCADE,
    threat_type     VARCHAR(30) NOT NULL,
    threat_level    VARCHAR(10) NOT NULL,
    detected_text   TEXT,
    source_package  VARCHAR(200),
    action_taken    VARCHAR(30),
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE threat_patterns (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pattern         TEXT NOT NULL,
    pattern_type    VARCHAR(20) NOT NULL,
    threat_level    VARCHAR(10) NOT NULL,
    category        VARCHAR(30) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

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
