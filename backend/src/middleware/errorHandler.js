const errorHandler = (err, req, res, _next) => {
  console.error(err.stack);

  const isProduction = process.env.NODE_ENV === 'production';

  res.status(err.status || 500).json({
    error: isProduction ? '서버 내부 오류가 발생했습니다.' : (err.message || '서버 내부 오류가 발생했습니다.'),
  });
};

module.exports = errorHandler;
