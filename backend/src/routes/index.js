const router = require('express').Router();
const authenticate = require('../middleware/auth');

// 공개 엔드포인트 (인증 불필요)
router.use('/auth', require('./auth'));
router.use('/education', require('./education'));

// 인증 필요 엔드포인트
router.use('/guardian', authenticate, require('./guardian'));
router.use('/threats', authenticate, require('./threats'));
router.use('/apps', authenticate, require('./apps'));
router.use('/urls', authenticate, require('./urls'));
router.use('/devices', authenticate, require('./devices'));
router.use('/whitelist', authenticate, require('./whitelist'));

module.exports = router;
