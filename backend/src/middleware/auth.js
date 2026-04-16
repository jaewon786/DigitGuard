const config = require('../config');
const { getFirebaseAdmin } = require('../services/firebase');

const authenticate = async (req, res, next) => {
  const authHeader = req.headers.authorization;

  // 개발 모드: 토큰 없이도 허용
  if (config.nodeEnv === 'development' && !authHeader) {
    req.user = { uid: 'dev-user' };
    return next();
  }

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ error: '인증 토큰이 필요합니다.' });
  }

  const token = authHeader.split(' ')[1];

  // Firebase Admin이 초기화되어 있으면 토큰 검증
  const admin = getFirebaseAdmin();
  if (admin) {
    try {
      const decodedToken = await admin.auth().verifyIdToken(token);
      req.user = decodedToken;
      return next();
    } catch (error) {
      return res.status(401).json({ error: '유효하지 않은 토큰입니다.' });
    }
  }

  // Firebase 미설정 시 개발용 통과
  req.user = { uid: token || 'dev-user' };
  next();
};

module.exports = authenticate;
