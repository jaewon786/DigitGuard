const dal = require('../db/dal');
const { notifyGuardianInstallRequest } = require('../services/notificationService');

const RISKY_PACKAGES = [
  'com.fake.antivirus',
  'com.scam.cleaner',
  'com.suspicious.optimizer',
];

exports.checkApp = async (req, res, next) => {
  try {
    const { packageName } = req.body;
    if (!packageName) {
      return res.status(400).json({ error: 'packageName이 필요합니다.' });
    }

    const isRisky = RISKY_PACKAGES.some((p) => packageName.includes(p));
    res.json({
      packageName,
      safe: !isRisky,
      riskScore: isRisky ? 85 : 10,
      reason: isRisky ? '의심스러운 앱입니다. 설치를 권장하지 않습니다.' : '검증된 앱입니다.',
    });
  } catch (err) {
    next(err);
  }
};

exports.installRequest = async (req, res, next) => {
  try {
    const { protectedId, packageName, appName, riskScore } = req.body;
    if (!protectedId || !packageName) {
      return res.status(400).json({ error: 'protectedId와 packageName이 필요합니다.' });
    }

    const request = await dal.createInstallRequest({
      protected_id: protectedId,
      package_name: packageName,
      app_name: appName || packageName,
      risk_score: riskScore || 50,
    });

    const notifyResults = await notifyGuardianInstallRequest(protectedId, {
      packageName,
      appName: appName || packageName,
      requestId: request.id,
    });

    res.status(201).json({
      message: '설치 승인 요청을 보냈습니다.',
      request,
      notifiedGuardians: notifyResults.length,
    });
  } catch (err) {
    next(err);
  }
};

exports.respondInstallRequest = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { decision } = req.body;

    if (!['approved', 'rejected'].includes(decision)) {
      return res.status(400).json({ error: 'decision은 approved 또는 rejected여야 합니다.' });
    }

    const request = await dal.findInstallRequestById(id);
    if (!request) {
      return res.status(404).json({ error: '요청을 찾을 수 없습니다.' });
    }

    const updated = await dal.updateInstallDecision(id, decision);
    res.json({
      message: decision === 'approved' ? '설치가 승인되었습니다.' : '설치가 거부되었습니다.',
      request: updated,
    });
  } catch (err) {
    next(err);
  }
};

exports.getWhitelist = async (req, res, next) => {
  try {
    const { userId } = req.params;
    // 승인된 앱 목록을 화이트리스트로 반환
    // TODO: dal.getApprovedApps(userId) 구현
    res.json({ userId, whitelist: [] });
  } catch (err) {
    next(err);
  }
};
