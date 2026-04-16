// 의심스러운 URL 패턴
const SUSPICIOUS_PATTERNS = [
  /bit\.ly\/[a-zA-Z0-9]+/i,
  /tinyurl\.com\/[a-zA-Z0-9]+/i,
  /\.(tk|ml|ga|cf|gq)\//i,
  /login.*\.php/i,
  /verify.*account/i,
  /secure.*update/i,
  /\.apk$/i,
  /download.*free/i,
];

// 알려진 피싱 도메인
const PHISHING_DOMAINS = new Set([
  'fake-bank.com',
  'security-alert.net',
  'account-verify.info',
  'free-prize.net',
  'login-update.com',
]);

function extractDomain(url) {
  try {
    const parsed = new URL(url.startsWith('http') ? url : 'https://' + url);
    return parsed.hostname.toLowerCase();
  } catch {
    return url.split('/')[0]?.toLowerCase() || '';
  }
}

exports.checkUrl = async (req, res, next) => {
  try {
    const { url } = req.body;
    if (!url) {
      return res.status(400).json({ error: 'URL이 필요합니다.' });
    }

    const threats = [];

    // 1. 피싱 도메인 확인
    const domain = extractDomain(url);
    if (PHISHING_DOMAINS.has(domain)) {
      threats.push('알려진 피싱 사이트입니다.');
    }

    // 2. 의심스러운 패턴 매칭
    for (const pattern of SUSPICIOUS_PATTERNS) {
      if (pattern.test(url)) {
        threats.push(`의심스러운 URL 패턴: ${pattern.source}`);
        break; // 하나만 매칭
      }
    }

    // 3. HTTPS 미사용 경고
    if (url.startsWith('http://') && !url.includes('localhost')) {
      threats.push('암호화되지 않은 연결(HTTP)을 사용합니다.');
    }

    // TODO: Google Safe Browsing API 연동
    // TODO: VirusTotal API 연동

    const safe = threats.length === 0;

    res.json({
      url,
      safe,
      riskLevel: threats.length >= 2 ? 'high' : threats.length === 1 ? 'medium' : 'low',
      threats,
      recommendation: safe
        ? '안전한 링크입니다.'
        : '위험한 링크입니다. 접속하지 마세요.',
      checkedAt: new Date().toISOString(),
    });
  } catch (err) {
    next(err);
  }
};
