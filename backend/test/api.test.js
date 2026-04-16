const { describe, it, before, after, beforeEach } = require('node:test');
const assert = require('node:assert/strict');
const http = require('http');
const app = require('../src/app');

let server;
let port;

function request(method, path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : '';
    const opts = {
      hostname: 'localhost',
      port,
      path: '/api/v1' + path,
      method,
      headers: { 'Content-Type': 'application/json' },
    };
    if (data) opts.headers['Content-Length'] = Buffer.byteLength(data);

    const req = http.request(opts, (res) => {
      let b = '';
      res.on('data', (d) => (b += d));
      res.on('end', () => {
        try {
          resolve({ status: res.statusCode, body: JSON.parse(b) });
        } catch {
          resolve({ status: res.statusCode, body: b });
        }
      });
    });
    req.on('error', reject);
    if (data) req.write(data);
    req.end();
  });
}

before(() => {
  return new Promise((resolve) => {
    server = app.listen(0, () => {
      port = server.address().port;
      resolve();
    });
  });
});

after(() => {
  return new Promise((resolve) => server.close(resolve));
});

// ── Auth ──────────────────────────────────────

describe('Auth API', () => {
  it('POST /auth/register — 정상 가입', async () => {
    const res = await request('POST', '/auth/register', {
      name: '테스트유저',
      phone: '010-1234-5678',
      role: 'protected',
      firebaseUid: `test-${Date.now()}`,
    });
    assert.equal(res.status, 201);
    assert.equal(res.body.user.name, '테스트유저');
    assert.equal(res.body.user.role, 'protected');
  });

  it('POST /auth/register — 이름 누락 시 400', async () => {
    const res = await request('POST', '/auth/register', { role: 'protected' });
    assert.equal(res.status, 400);
  });

  it('POST /auth/register — 잘못된 role 시 400', async () => {
    const res = await request('POST', '/auth/register', { name: 'X', role: 'admin' });
    assert.equal(res.status, 400);
  });

  it('POST /auth/register — 이름 50자 초과 시 400', async () => {
    const res = await request('POST', '/auth/register', { name: 'A'.repeat(51), role: 'protected' });
    assert.equal(res.status, 400);
  });

  it('POST /auth/register — 중복 가입 시 409', async () => {
    const uid = `dup-${Date.now()}`;
    await request('POST', '/auth/register', { name: 'A', role: 'protected', firebaseUid: uid });
    const res = await request('POST', '/auth/register', { name: 'B', role: 'guardian', firebaseUid: uid });
    assert.equal(res.status, 409);
  });

  it('POST /auth/login — 미등록 사용자 시 404', async () => {
    const res = await request('POST', '/auth/login', { firebaseUid: 'nonexistent-user' });
    assert.equal(res.status, 404);
  });

  it('POST /auth/login — 정상 로그인', async () => {
    const uid = `login-${Date.now()}`;
    await request('POST', '/auth/register', { name: 'L', role: 'protected', firebaseUid: uid });
    const res = await request('POST', '/auth/login', { firebaseUid: uid });
    assert.equal(res.status, 200);
    assert.ok(res.body.token);
  });
});

// ── Threats ───────────────────────────────────

describe('Threats API', () => {
  it('POST /threats/analyze — 위험 텍스트 분석', async () => {
    const res = await request('POST', '/threats/analyze', {
      text: '핸드폰이 바이러스에 감염되었습니다',
    });
    assert.equal(res.status, 200);
    assert.equal(res.body.threatLevel, 'high');
    assert.equal(res.body.shouldNotifyGuardian, true);
    assert.ok(res.body.matchedPatterns.length > 0);
  });

  it('POST /threats/analyze — 안전한 텍스트', async () => {
    const res = await request('POST', '/threats/analyze', {
      text: '오늘 날씨가 좋습니다',
    });
    assert.equal(res.status, 200);
    assert.equal(res.body.threatLevel, 'none');
    assert.equal(res.body.shouldNotifyGuardian, false);
  });

  it('POST /threats/analyze — 텍스트 누락 시 400', async () => {
    const res = await request('POST', '/threats/analyze', {});
    assert.equal(res.status, 400);
  });

  it('GET /threats/patterns/sync — 패턴 동기화', async () => {
    const res = await request('GET', '/threats/patterns/sync');
    assert.equal(res.status, 200);
    assert.ok(res.body.count >= 14);
    assert.ok(Array.isArray(res.body.patterns));
  });

  it('POST /threats/log — 배치 로그 (필수필드 검증)', async () => {
    const res = await request('POST', '/threats/log', {
      logs: [
        { user_id: 'u1', threat_type: 'fake_virus', threat_level: 'high' },
        { bad: 'data' },
        { user_id: 'u2', threat_type: 'phishing', threat_level: 'medium' },
      ],
    });
    assert.equal(res.status, 200);
    assert.equal(res.body.count, 2); // 유효한 2건만 저장
  });
});

// ── Guardian ─────────────────────────────────

describe('Guardian API', () => {
  let guardianId, protectedId;

  before(async () => {
    const g = await request('POST', '/auth/register', {
      name: '보호자', role: 'guardian', firebaseUid: `g-${Date.now()}`,
    });
    const p = await request('POST', '/auth/register', {
      name: '피보호자', role: 'protected', firebaseUid: `p-${Date.now()}`,
    });
    guardianId = g.body.user.id;
    protectedId = p.body.user.id;
  });

  it('POST /guardian/link — 연결 코드 발급 (6자리)', async () => {
    const res = await request('POST', '/guardian/link', { guardianId });
    assert.equal(res.status, 201);
    assert.equal(res.body.linkCode.length, 6);
  });

  it('POST /guardian/link/accept-by-code — 코드 연결', async () => {
    const link = await request('POST', '/guardian/link', { guardianId });
    const res = await request('POST', '/guardian/link/accept-by-code', {
      code: link.body.linkCode,
      protectedId,
    });
    assert.equal(res.status, 200);
    assert.ok(res.body.guardianName);
  });

  it('PUT /guardian/link/:id/accept — 이미 수락된 링크 재수락 방지', async () => {
    const link = await request('POST', '/guardian/link', { guardianId, protectedId });
    await request('PUT', `/guardian/link/${link.body.linkId}/accept`);
    const res = await request('PUT', `/guardian/link/${link.body.linkId}/accept`);
    assert.equal(res.status, 400);
  });

  it('GET /guardian/dashboard/:userId — 대시보드 조회', async () => {
    const res = await request('GET', `/guardian/dashboard/${protectedId}`);
    assert.equal(res.status, 200);
    assert.ok('status' in res.body);
    assert.ok(Array.isArray(res.body.recentThreats));
  });
});

// ── URLs ─────────────────────────────────────

describe('URL Check API', () => {
  it('POST /urls/check — 안전 URL', async () => {
    const res = await request('POST', '/urls/check', { url: 'https://google.com' });
    assert.equal(res.status, 200);
    assert.equal(res.body.safe, true);
  });

  it('POST /urls/check — 피싱 URL', async () => {
    const res = await request('POST', '/urls/check', { url: 'http://fake-bank.com/login.php' });
    assert.equal(res.status, 200);
    assert.equal(res.body.safe, false);
    assert.ok(res.body.threats.length > 0);
  });

  it('POST /urls/check — URL 누락 시 400', async () => {
    const res = await request('POST', '/urls/check', {});
    assert.equal(res.status, 400);
  });
});

// ── Apps ──────────────────────────────────────

describe('Apps API', () => {
  it('POST /apps/check — 앱 검사', async () => {
    const res = await request('POST', '/apps/check', { packageName: 'com.kakao.talk' });
    assert.equal(res.status, 200);
    assert.equal(res.body.safe, true);
  });

  it('POST /apps/check — 위험 앱 검사', async () => {
    const res = await request('POST', '/apps/check', { packageName: 'com.fake.antivirus' });
    assert.equal(res.status, 200);
    assert.equal(res.body.safe, false);
  });

  it('POST /apps/install-request — 설치 승인 요청', async () => {
    const u = await request('POST', '/auth/register', {
      name: 'IR', role: 'protected', firebaseUid: `ir-${Date.now()}`,
    });
    const res = await request('POST', '/apps/install-request', {
      protectedId: u.body.user.id,
      packageName: 'com.test.app',
      appName: '테스트앱',
    });
    assert.equal(res.status, 201);
    assert.ok(res.body.request.id);
  });
});

// ── Education ────────────────────────────────

describe('Education API', () => {
  it('GET /education/contents — 목록 조회', async () => {
    const res = await request('GET', '/education/contents');
    assert.equal(res.status, 200);
    assert.equal(res.body.count, 5);
    // 목록에는 steps 미포함
    assert.equal(res.body.contents[0].steps, undefined);
  });

  it('GET /education/contents/:id — 상세 조회', async () => {
    const res = await request('GET', '/education/contents/1');
    assert.equal(res.status, 200);
    assert.ok(Array.isArray(res.body.steps));
    assert.ok(res.body.steps.length > 0);
  });

  it('GET /education/contents/:id — 존재하지 않는 ID', async () => {
    const res = await request('GET', '/education/contents/999');
    assert.equal(res.status, 404);
  });
});

// ── Whitelist ────────────────────────────────

describe('Whitelist API', () => {
  let userId;

  before(async () => {
    const u = await request('POST', '/auth/register', {
      name: 'WL', role: 'protected', firebaseUid: `wl-${Date.now()}`,
    });
    userId = u.body.user.id;
  });

  it('POST /whitelist/:userId — 추가', async () => {
    const res = await request('POST', `/whitelist/${userId}`, {
      packageName: 'com.test.wl',
      appName: '테스트',
    });
    assert.equal(res.status, 201);
  });

  it('GET /whitelist/:userId — 조회', async () => {
    const res = await request('GET', `/whitelist/${userId}`);
    assert.equal(res.status, 200);
    assert.equal(res.body.count, 1);
  });

  it('POST /whitelist/:userId — 중복 추가 409', async () => {
    const res = await request('POST', `/whitelist/${userId}`, { packageName: 'com.test.wl' });
    assert.equal(res.status, 409);
  });

  it('DELETE /whitelist/:userId/:pkg — 삭제', async () => {
    const res = await request('DELETE', `/whitelist/${userId}/com.test.wl`);
    assert.equal(res.status, 200);
  });
});

// ── Health ───────────────────────────────────

describe('Health Check', () => {
  it('GET /health — 서버 상태 확인', async () => {
    return new Promise((resolve, reject) => {
      http.get(`http://localhost:${port}/health`, (res) => {
        let b = '';
        res.on('data', (d) => (b += d));
        res.on('end', () => {
          const data = JSON.parse(b);
          assert.equal(res.statusCode, 200);
          assert.equal(data.status, 'ok');
          resolve();
        });
      }).on('error', reject);
    });
  });
});
