const dal = require('../db/dal');

exports.register = async (req, res, next) => {
  try {
    const { name, phone, role, firebaseUid } = req.body;

    if (!name || !role) {
      return res.status(400).json({ error: '이름과 역할(role)은 필수입니다.' });
    }
    if (!['protected', 'guardian'].includes(role)) {
      return res.status(400).json({ error: 'role은 protected 또는 guardian이어야 합니다.' });
    }
    if (name.length > 50) {
      return res.status(400).json({ error: '이름은 50자 이하여야 합니다.' });
    }
    if (phone && !/^[\d\-+() ]{0,20}$/.test(phone)) {
      return res.status(400).json({ error: '올바른 전화번호 형식이 아닙니다.' });
    }

    const uid = firebaseUid || `dev-${Date.now()}`;
    const existing = await dal.findUserByFirebaseUid(uid);
    if (existing) {
      return res.status(409).json({ error: '이미 등록된 사용자입니다.' });
    }

    const user = await dal.createUser({ firebase_uid: uid, name, phone: phone || '', role });
    res.status(201).json({ message: '회원가입 완료', user });
  } catch (err) {
    next(err);
  }
};

exports.login = async (req, res, next) => {
  try {
    const { firebaseUid } = req.body;

    if (!firebaseUid) {
      return res.status(400).json({ error: 'firebaseUid가 필요합니다.' });
    }

    const user = await dal.findUserByFirebaseUid(firebaseUid);
    if (!user) {
      return res.status(404).json({ error: '등록되지 않은 사용자입니다.' });
    }

    res.json({ message: '로그인 성공', user, token: 'dev-token-' + user.id });
  } catch (err) {
    next(err);
  }
};

exports.deleteAccount = async (req, res, next) => {
  try {
    const uid = req.user?.uid;
    if (!uid) {
      return res.status(401).json({ error: '인증이 필요합니다.' });
    }

    const deleted = await dal.deleteUser(uid);
    if (!deleted) {
      return res.status(404).json({ error: '사용자를 찾을 수 없습니다.' });
    }

    res.json({ message: '계정이 삭제되었습니다.' });
  } catch (err) {
    next(err);
  }
};
