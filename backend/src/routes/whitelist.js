const router = require('express').Router();
const store = require('../db/inMemoryStore');

// 화이트리스트에 없으면 초기화
function ensureWhitelist(userId) {
  if (!store.whitelists) store.whitelists = {};
  if (!store.whitelists[userId]) store.whitelists[userId] = [];
  return store.whitelists[userId];
}

// 화이트리스트 조회
router.get('/:userId', (req, res, next) => {
  try {
    const list = ensureWhitelist(req.params.userId);
    res.json({ userId: req.params.userId, whitelist: list, count: list.length });
  } catch (err) {
    next(err);
  }
});

// 화이트리스트 앱 추가
router.post('/:userId', (req, res, next) => {
  try {
    const { packageName, appName } = req.body;
    if (!packageName) {
      return res.status(400).json({ error: 'packageName이 필요합니다.' });
    }
    const list = ensureWhitelist(req.params.userId);
    const existing = list.find((a) => a.packageName === packageName);
    if (existing) {
      return res.status(409).json({ error: '이미 화이트리스트에 있습니다.' });
    }
    const entry = { packageName, appName: appName || packageName, addedAt: new Date().toISOString() };
    list.push(entry);
    res.status(201).json({ message: '화이트리스트에 추가되었습니다.', entry });
  } catch (err) {
    next(err);
  }
});

// 화이트리스트에서 앱 제거
router.delete('/:userId/:packageName', (req, res, next) => {
  try {
    const list = ensureWhitelist(req.params.userId);
    const idx = list.findIndex((a) => a.packageName === req.params.packageName);
    if (idx === -1) {
      return res.status(404).json({ error: '화이트리스트에 없는 앱입니다.' });
    }
    list.splice(idx, 1);
    res.json({ message: '화이트리스트에서 제거되었습니다.' });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
