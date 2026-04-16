// 간단한 인메모리 Rate Limiter (프로덕션에서는 Redis 기반 사용 권장)

function rateLimiter({ windowMs = 60 * 1000, max = 60, message = '요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.' } = {}) {
  // 각 Rate Limiter 인스턴스마다 독립적인 Map 사용
  const requests = new Map();

  // 주기적 정리
  const cleanup = setInterval(() => {
    const now = Date.now();
    for (const [key, entry] of requests) {
      if (now - entry.windowStart > windowMs) {
        requests.delete(key);
      }
    }
  }, Math.max(windowMs, 60 * 1000));
  cleanup.unref(); // 서버 종료를 방해하지 않도록

  return (req, res, next) => {
    const key = req.ip || req.connection.remoteAddress;
    const now = Date.now();

    let entry = requests.get(key);
    if (!entry || now - entry.windowStart > windowMs) {
      entry = { windowStart: now, count: 0 };
      requests.set(key, entry);
    }

    entry.count++;

    if (entry.count > max) {
      return res.status(429).json({ error: message });
    }

    res.setHeader('X-RateLimit-Limit', max);
    res.setHeader('X-RateLimit-Remaining', Math.max(0, max - entry.count));
    next();
  };
}

// 엔드포인트별 제한
const apiLimiter = rateLimiter({ windowMs: 60 * 1000, max: 100 });
const authLimiter = rateLimiter({ windowMs: 15 * 60 * 1000, max: 20, message: '인증 요청이 너무 많습니다.' });
const analyzeLimiter = rateLimiter({ windowMs: 60 * 1000, max: 30, message: '분석 요청이 너무 많습니다.' });

module.exports = { rateLimiter, apiLimiter, authLimiter, analyzeLimiter };
