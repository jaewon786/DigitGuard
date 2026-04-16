/**
 * Data Access Layer — PostgreSQL 연결 시 SQL, 미연결 시 인메모리 자동 전환
 */
const { query } = require('./index');
const store = require('./inMemoryStore');

let usePostgres = false;

function setUsePostgres(val) {
  usePostgres = val;
}

// ── Users ────────────────────────────────────

async function createUser({ firebase_uid, name, phone, role }) {
  if (usePostgres) {
    const res = await query(
      'INSERT INTO users (firebase_uid, name, phone, role) VALUES ($1, $2, $3, $4) RETURNING *',
      [firebase_uid, name, phone || '', role]
    );
    return res.rows[0];
  }
  return store.createUser({ firebase_uid, name, phone, role });
}

async function findUserByFirebaseUid(uid) {
  if (usePostgres) {
    const res = await query('SELECT * FROM users WHERE firebase_uid = $1', [uid]);
    return res.rows[0] || null;
  }
  return store.findUserByFirebaseUid(uid) || null;
}

async function findUserById(id) {
  if (usePostgres) {
    const res = await query('SELECT * FROM users WHERE id = $1', [id]);
    return res.rows[0] || null;
  }
  return store.findUserById(id) || null;
}

async function deleteUser(uid) {
  if (usePostgres) {
    const res = await query('DELETE FROM users WHERE firebase_uid = $1 RETURNING id', [uid]);
    return res.rows[0] || null;
  }
  const idx = store.users.findIndex((u) => u.firebase_uid === uid);
  if (idx === -1) return null;
  const userId = store.users[idx].id;
  store.guardianLinks = store.guardianLinks.filter(
    (l) => l.guardian_id !== userId && l.protected_id !== userId
  );
  store.threatLogs = store.threatLogs.filter((l) => l.user_id !== userId);
  store.appInstallRequests = store.appInstallRequests.filter((r) => r.protected_id !== userId);
  if (store.whitelists) delete store.whitelists[userId];
  store.users.splice(idx, 1);
  return { id: userId };
}

// ── Guardian Links ───────────────────────────

async function createGuardianLink({ guardian_id, protected_id, link_code }) {
  if (usePostgres) {
    const res = await query(
      'INSERT INTO guardian_links (guardian_id, protected_id, link_code, status) VALUES ($1, $2, $3, $4) RETURNING *',
      [guardian_id, protected_id, link_code, 'pending']
    );
    return res.rows[0];
  }
  return store.createGuardianLink({ guardian_id, protected_id, link_code });
}

async function findLinkById(id) {
  if (usePostgres) {
    const res = await query('SELECT * FROM guardian_links WHERE id = $1', [id]);
    return res.rows[0] || null;
  }
  return store.findLinkById(id) || null;
}

async function findLinkByCode(code) {
  if (usePostgres) {
    const res = await query(
      "SELECT * FROM guardian_links WHERE link_code = $1 AND status = 'pending'",
      [code]
    );
    return res.rows[0] || null;
  }
  return store.findLinkByCode(code) || null;
}

async function updateLinkStatus(id, status, updates = {}) {
  if (usePostgres) {
    const sets = ['status = $2'];
    const params = [id, status];
    let idx = 3;
    if (updates.protected_id) {
      sets.push(`protected_id = $${idx}`);
      params.push(updates.protected_id);
      idx++;
    }
    if (status === 'active') {
      sets.push('link_code = NULL');
    }
    const res = await query(
      `UPDATE guardian_links SET ${sets.join(', ')} WHERE id = $1 RETURNING *`,
      params
    );
    return res.rows[0];
  }
  const link = store.findLinkById(id);
  if (!link) return null;
  link.status = status;
  if (updates.protected_id) link.protected_id = updates.protected_id;
  if (status === 'active') link.link_code = null;
  return link;
}

async function getProtectedUsers(guardianId) {
  if (usePostgres) {
    const res = await query(
      `SELECT u.*, gl.id as link_id FROM users u
       JOIN guardian_links gl ON gl.protected_id = u.id
       WHERE gl.guardian_id = $1 AND gl.status = 'active'`,
      [guardianId]
    );
    return res.rows.map((r) => ({ ...r, linkId: r.link_id }));
  }
  return store.getProtectedUsers(guardianId);
}

// ── Threat Logs ──────────────────────────────

async function createThreatLog({ user_id, threat_type, threat_level, detected_text, source_package, action_taken }) {
  if (usePostgres) {
    const res = await query(
      'INSERT INTO threat_logs (user_id, threat_type, threat_level, detected_text, source_package, action_taken) VALUES ($1,$2,$3,$4,$5,$6) RETURNING *',
      [user_id, threat_type, threat_level, (detected_text || '').substring(0, 500), source_package || '', action_taken || 'logged']
    );
    return res.rows[0];
  }
  return store.createThreatLog({ user_id, threat_type, threat_level, detected_text, source_package, action_taken });
}

async function getThreatHistory(userId, limit = 50) {
  if (usePostgres) {
    const res = await query(
      'SELECT * FROM threat_logs WHERE user_id = $1 ORDER BY created_at DESC LIMIT $2',
      [userId, limit]
    );
    return res.rows;
  }
  return store.getThreatHistory(userId).slice(0, limit);
}

// ── Threat Patterns ──────────────────────────

async function getActivePatterns() {
  if (usePostgres) {
    const res = await query("SELECT * FROM threat_patterns WHERE is_active = TRUE");
    return res.rows;
  }
  return store.getActivePatterns();
}

// ── App Install Requests ─────────────────────

async function createInstallRequest({ protected_id, package_name, app_name, risk_score }) {
  if (usePostgres) {
    const res = await query(
      "INSERT INTO app_install_requests (protected_id, package_name, app_name, risk_score, guardian_decision) VALUES ($1,$2,$3,$4,'pending') RETURNING *",
      [protected_id, package_name, app_name, risk_score || 50]
    );
    return res.rows[0];
  }
  return store.createInstallRequest({ protected_id, package_name, app_name, risk_score });
}

async function findInstallRequestById(id) {
  if (usePostgres) {
    const res = await query('SELECT * FROM app_install_requests WHERE id = $1', [id]);
    return res.rows[0] || null;
  }
  return store.findInstallRequestById(id) || null;
}

async function updateInstallDecision(id, decision) {
  if (usePostgres) {
    const res = await query(
      'UPDATE app_install_requests SET guardian_decision = $2, decided_at = NOW() WHERE id = $1 RETURNING *',
      [id, decision]
    );
    return res.rows[0];
  }
  const req = store.findInstallRequestById(id);
  if (!req) return null;
  req.guardian_decision = decision;
  req.decided_at = new Date();
  return req;
}

async function getPendingInstalls(protectedId) {
  if (usePostgres) {
    const res = await query(
      "SELECT * FROM app_install_requests WHERE protected_id = $1 AND guardian_decision = 'pending'",
      [protectedId]
    );
    return res.rows;
  }
  return store.appInstallRequests.filter(
    (r) => r.protected_id === protectedId && r.guardian_decision === 'pending'
  );
}

module.exports = {
  setUsePostgres,
  createUser, findUserByFirebaseUid, findUserById, deleteUser,
  createGuardianLink, findLinkById, findLinkByCode, updateLinkStatus, getProtectedUsers,
  createThreatLog, getThreatHistory,
  getActivePatterns,
  createInstallRequest, findInstallRequestById, updateInstallDecision, getPendingInstalls,
};
