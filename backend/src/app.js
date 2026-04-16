const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('../swagger.json');
const routes = require('./routes');
const errorHandler = require('./middleware/errorHandler');
const { apiLimiter } = require('./middleware/rateLimiter');

const app = express();

// 보안 헤더
app.use(helmet());

// CORS 설정
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization'],
}));

// 요청 크기 제한
app.use(express.json({ limit: '100kb' }));

// 전역 Rate Limiting
app.use('/api', apiLimiter);

// Swagger UI
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

// API 라우트
app.use('/api/v1', routes);

// 루트 랜딩 페이지
app.get('/', (req, res) => {
  res.send(`<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DigitGuard API Server</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; background: #f8f9fa; color: #212529; min-height: 100vh; display: flex; align-items: center; justify-content: center; }
    .container { max-width: 640px; padding: 48px 32px; text-align: center; }
    .shield { font-size: 72px; margin-bottom: 16px; }
    h1 { font-size: 32px; margin-bottom: 8px; color: #1565C0; }
    .subtitle { font-size: 18px; color: #666; margin-bottom: 40px; }
    .links { display: flex; flex-direction: column; gap: 12px; }
    a { display: block; padding: 16px 24px; background: #fff; border: 1px solid #dee2e6; border-radius: 12px; text-decoration: none; color: #333; font-size: 16px; transition: all 0.2s; }
    a:hover { border-color: #1565C0; background: #e3f2fd; }
    .tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; margin-left: 8px; }
    .get { background: #e8f5e9; color: #2e7d32; }
    .post { background: #e3f2fd; color: #1565c0; }
    .status { margin-top: 32px; padding: 12px; background: #e8f5e9; border-radius: 8px; color: #2e7d32; font-size: 14px; }
  </style>
</head>
<body>
  <div class="container">
    <div class="shield">🛡️</div>
    <h1>DigitGuard API</h1>
    <p class="subtitle">디지털 취약 계층 보호 앱 백엔드 서버</p>
    <div class="links">
      <a href="/api-docs">📖 Swagger API 문서 <span class="tag get">Interactive</span></a>
      <a href="/health">💚 서버 상태 확인 <span class="tag get">GET</span></a>
      <a href="/api/v1/education/contents">📚 교육 콘텐츠 목록 <span class="tag get">GET</span></a>
      <a href="/api/v1/threats/patterns/sync">🔍 위험 패턴 목록 <span class="tag get">GET</span></a>
    </div>
    <div class="status">✅ 서버 정상 동작 중</div>
  </div>
</body>
</html>`);
});

// 헬스 체크
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// 에러 핸들링
app.use(errorHandler);

module.exports = app;
