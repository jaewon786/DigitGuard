const router = require('express').Router();
const store = require('../db/inMemoryStore');

// FCM 토큰 등록/갱신
router.post('/fcm-token', (req, res, next) => {
  try {
    const { userId, fcmToken } = req.body;
    if (!userId || !fcmToken) {
      return res.status(400).json({ error: 'userId와 fcmToken이 필요합니다.' });
    }

    const user = store.findUserById(userId);
    if (!user) {
      return res.status(404).json({ error: '사용자를 찾을 수 없습니다.' });
    }

    user.fcmToken = fcmToken;
    res.json({ message: 'FCM 토큰이 등록되었습니다.' });
  } catch (err) {
    next(err);
  }
});

// FCM 토큰 삭제 (로그아웃 시)
router.delete('/fcm-token', (req, res, next) => {
  try {
    const { userId } = req.body;
    const user = store.findUserById(userId);
    if (user) {
      user.fcmToken = null;
    }
    res.json({ message: 'FCM 토큰이 삭제되었습니다.' });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
