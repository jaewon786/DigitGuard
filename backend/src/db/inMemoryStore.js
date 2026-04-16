// PostgreSQL 미연결 시 인메모리 저장소 (개발용)
const crypto = require('crypto');

function generateId() {
  return crypto.randomUUID ? crypto.randomUUID() : Date.now().toString(36) + Math.random().toString(36).slice(2);
}

const store = {
  users: [],
  guardianLinks: [],
  threatLogs: [],
  threatPatterns: [
    { id: generateId(), pattern: '바이러스.*발견', patternType: 'regex', threatLevel: 'high', category: 'fake_virus', isActive: true },
    { id: generateId(), pattern: '악성코드.*노출', patternType: 'regex', threatLevel: 'high', category: 'fake_virus', isActive: true },
    { id: generateId(), pattern: '핸드폰.*감염', patternType: 'regex', threatLevel: 'high', category: 'fake_virus', isActive: true },
    { id: generateId(), pattern: '지금.*치료', patternType: 'regex', threatLevel: 'high', category: 'fake_virus', isActive: true },
    { id: generateId(), pattern: '개인정보.*유출', patternType: 'regex', threatLevel: 'high', category: 'fake_security', isActive: true },
    { id: generateId(), pattern: '보안.*앱.*설치', patternType: 'regex', threatLevel: 'medium', category: 'fake_security', isActive: true },
    { id: generateId(), pattern: '긴급.*업데이트', patternType: 'regex', threatLevel: 'medium', category: 'fake_security', isActive: true },
    { id: generateId(), pattern: '배터리.*손상', patternType: 'regex', threatLevel: 'medium', category: 'fake_virus', isActive: true },
    { id: generateId(), pattern: '기기.*해킹', patternType: 'regex', threatLevel: 'high', category: 'fake_security', isActive: true },
    { id: generateId(), pattern: '계좌.*정지', patternType: 'regex', threatLevel: 'high', category: 'phishing', isActive: true },
    { id: generateId(), pattern: '본인.*확인.*링크', patternType: 'regex', threatLevel: 'high', category: 'phishing', isActive: true },
    { id: generateId(), pattern: '택배.*조회', patternType: 'regex', threatLevel: 'medium', category: 'phishing', isActive: true },
    { id: generateId(), pattern: '당첨.*축하', patternType: 'regex', threatLevel: 'high', category: 'phishing', isActive: true },
    { id: generateId(), pattern: '무료.*쿠폰.*지급', patternType: 'regex', threatLevel: 'medium', category: 'phishing', isActive: true },
  ],
  appInstallRequests: [],
  whitelists: {},
};

// Users
store.findUserByFirebaseUid = (uid) => store.users.find((u) => u.firebase_uid === uid);
store.findUserById = (id) => store.users.find((u) => u.id === id);
store.createUser = ({ firebase_uid, name, phone, role }) => {
  const user = { id: generateId(), firebase_uid, name, phone, role, created_at: new Date(), updated_at: new Date() };
  store.users.push(user);
  return user;
};

// Guardian Links
store.createGuardianLink = ({ guardian_id, protected_id, link_code }) => {
  const link = { id: generateId(), guardian_id, protected_id, status: 'pending', link_code, created_at: new Date() };
  store.guardianLinks.push(link);
  return link;
};
store.findLinkById = (id) => store.guardianLinks.find((l) => l.id === id);
store.findLinkByCode = (code) => store.guardianLinks.find((l) => l.link_code === code && l.status === 'pending');
store.getProtectedUsers = (guardianId) => {
  const links = store.guardianLinks.filter((l) => l.guardian_id === guardianId && l.status === 'active');
  return links
    .map((l) => {
      const user = store.findUserById(l.protected_id);
      if (!user) return null;
      return { ...user, linkId: l.id };
    })
    .filter(Boolean);
};

// Threat Logs — ID 오버라이드 방지
store.createThreatLog = ({ user_id, threat_type, threat_level, detected_text, source_package, action_taken }) => {
  const entry = {
    id: generateId(),
    user_id, threat_type, threat_level,
    detected_text: (detected_text || '').substring(0, 500),
    source_package: source_package || '',
    action_taken: action_taken || 'logged',
    created_at: new Date(),
  };
  store.threatLogs.push(entry);
  return entry;
};
store.getThreatHistory = (userId) => store.threatLogs.filter((l) => l.user_id === userId).sort((a, b) => b.created_at - a.created_at);

// Threat Patterns
store.getActivePatterns = () => store.threatPatterns.filter((p) => p.isActive);

// App Install Requests — ID 오버라이드 방지
store.createInstallRequest = ({ protected_id, package_name, app_name, risk_score }) => {
  const entry = {
    id: generateId(),
    protected_id, package_name, app_name,
    risk_score: risk_score || 50,
    guardian_decision: 'pending',
    created_at: new Date(),
  };
  store.appInstallRequests.push(entry);
  return entry;
};
store.findInstallRequestById = (id) => store.appInstallRequests.find((r) => r.id === id);

module.exports = store;
