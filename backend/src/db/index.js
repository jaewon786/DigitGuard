const { Pool } = require('pg');
const config = require('../config');

const pool = new Pool(config.db);

pool.on('error', (err) => {
  console.error('PostgreSQL 연결 오류:', err.message);
});

const query = (text, params) => pool.query(text, params);

const getClient = () => pool.connect();

// DB 연결 테스트
const testConnection = async () => {
  try {
    const client = await pool.connect();
    await client.query('SELECT NOW()');
    client.release();
    console.log('PostgreSQL 연결 성공');
    return true;
  } catch (err) {
    console.warn('PostgreSQL 연결 실패 (인메모리 모드로 전환):', err.message);
    return false;
  }
};

// 테이블 초기화 (존재하지 않으면 생성)
const initTables = async () => {
  const createSQL = `
    CREATE TABLE IF NOT EXISTS users (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      firebase_uid VARCHAR(128) UNIQUE NOT NULL,
      name VARCHAR(50) NOT NULL,
      phone VARCHAR(20),
      role VARCHAR(10) NOT NULL CHECK (role IN ('protected', 'guardian')),
      created_at TIMESTAMPTZ DEFAULT NOW(),
      updated_at TIMESTAMPTZ DEFAULT NOW()
    );

    CREATE TABLE IF NOT EXISTS guardian_links (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      guardian_id UUID REFERENCES users(id) ON DELETE CASCADE,
      protected_id UUID REFERENCES users(id) ON DELETE CASCADE,
      status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'active', 'revoked')),
      link_code VARCHAR(8),
      created_at TIMESTAMPTZ DEFAULT NOW(),
      UNIQUE(guardian_id, protected_id)
    );

    CREATE TABLE IF NOT EXISTS threat_logs (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      user_id UUID REFERENCES users(id) ON DELETE CASCADE,
      threat_type VARCHAR(30) NOT NULL,
      threat_level VARCHAR(10) NOT NULL,
      detected_text TEXT,
      source_package VARCHAR(200),
      action_taken VARCHAR(30),
      created_at TIMESTAMPTZ DEFAULT NOW()
    );

    CREATE TABLE IF NOT EXISTS threat_patterns (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      pattern TEXT NOT NULL,
      pattern_type VARCHAR(20) NOT NULL,
      threat_level VARCHAR(10) NOT NULL,
      category VARCHAR(30) NOT NULL,
      is_active BOOLEAN DEFAULT TRUE,
      created_at TIMESTAMPTZ DEFAULT NOW(),
      updated_at TIMESTAMPTZ DEFAULT NOW()
    );

    CREATE TABLE IF NOT EXISTS app_install_requests (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      protected_id UUID REFERENCES users(id),
      package_name VARCHAR(200) NOT NULL,
      app_name VARCHAR(200),
      risk_score INTEGER CHECK (risk_score BETWEEN 0 AND 100),
      guardian_decision VARCHAR(10) CHECK (guardian_decision IN ('approved', 'rejected', 'pending')),
      decided_at TIMESTAMPTZ,
      created_at TIMESTAMPTZ DEFAULT NOW()
    );
  `;

  try {
    await query(createSQL);
    console.log('DB 테이블 초기화 완료');
  } catch (err) {
    console.warn('DB 테이블 초기화 실패:', err.message);
  }
};

module.exports = { pool, query, getClient, testConnection, initTables };
