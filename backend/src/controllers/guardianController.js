const crypto = require('crypto');
const dal = require('../db/dal');

function generateLinkCode() {
  return crypto.randomBytes(3).toString('hex').toUpperCase();
}

exports.linkRequest = async (req, res, next) => {
  try {
    const { guardianId, protectedId } = req.body;

    if (!guardianId) {
      return res.status(400).json({ error: 'guardianId가 필요합니다.' });
    }

    if (!protectedId) {
      const linkCode = generateLinkCode();
      const link = await dal.createGuardianLink({ guardian_id: guardianId, protected_id: null, link_code: linkCode });
      return res.status(201).json({
        message: '연결 코드가 발급되었습니다. 피보호자에게 이 코드를 알려주세요.',
        linkCode,
        linkId: link.id,
      });
    }

    const link = await dal.createGuardianLink({ guardian_id: guardianId, protected_id: protectedId, link_code: null });
    res.status(201).json({ message: '연결 요청을 보냈습니다.', linkId: link.id });
  } catch (err) {
    next(err);
  }
};

exports.acceptLink = async (req, res, next) => {
  try {
    const { id } = req.params;
    const link = await dal.findLinkById(id);
    if (!link) {
      return res.status(404).json({ error: '연결 요청을 찾을 수 없습니다.' });
    }
    if (link.status !== 'pending') {
      return res.status(400).json({ error: `이미 ${link.status === 'active' ? '수락된' : '취소된'} 연결입니다.` });
    }
    const updated = await dal.updateLinkStatus(id, 'active');
    res.json({ message: '연결이 수락되었습니다.', link: updated });
  } catch (err) {
    next(err);
  }
};

exports.acceptByCode = async (req, res, next) => {
  try {
    const { code, protectedId } = req.body;
    if (!code || !protectedId) {
      return res.status(400).json({ error: 'code와 protectedId가 필요합니다.' });
    }

    const link = await dal.findLinkByCode(code);
    if (!link) {
      return res.status(404).json({ error: '유효하지 않은 연결 코드입니다.' });
    }

    const updated = await dal.updateLinkStatus(link.id, 'active', { protected_id: protectedId });
    const guardian = await dal.findUserById(link.guardian_id);

    res.json({
      message: '보호자와 연결되었습니다.',
      guardianName: guardian?.name || '보호자',
      link: updated,
    });
  } catch (err) {
    next(err);
  }
};

exports.getProtectedUsers = async (req, res, next) => {
  try {
    const guardianId = req.query.guardianId;
    if (!guardianId) {
      return res.status(400).json({ error: 'guardianId 쿼리 파라미터가 필요합니다.' });
    }
    const users = await dal.getProtectedUsers(guardianId);
    res.json({ users });
  } catch (err) {
    next(err);
  }
};

exports.getDashboard = async (req, res, next) => {
  try {
    const { userId } = req.params;
    const user = await dal.findUserById(userId);
    if (!user) {
      return res.status(404).json({ error: '사용자를 찾을 수 없습니다.' });
    }

    const recentThreats = await dal.getThreatHistory(userId, 10);
    const pendingInstalls = await dal.getPendingInstalls(userId);

    res.json({
      userId,
      userName: user.name,
      status: recentThreats.some((t) => t.threat_level === 'high') ? 'warning' : 'safe',
      recentThreats,
      pendingInstalls,
      lastChecked: new Date().toISOString(),
    });
  } catch (err) {
    next(err);
  }
};
