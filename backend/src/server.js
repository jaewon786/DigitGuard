const app = require('./app');
const config = require('./config');
const db = require('./db');
const dal = require('./db/dal');

async function start() {
  // DB 연결 시도 (실패 시 인메모리 모드)
  const dbConnected = await db.testConnection();
  if (dbConnected) {
    await db.initTables();
  }
  dal.setUsePostgres(dbConnected);
  app.locals.dbConnected = dbConnected;

  app.listen(config.port, () => {
    console.log(`DigitGuard 백엔드 서버 실행 중: http://localhost:${config.port}`);
    console.log(`Swagger API 문서: http://localhost:${config.port}/api-docs`);
    console.log(`DB 모드: ${dbConnected ? 'PostgreSQL' : '인메모리 (개발용)'}`);
  });
}

start();
