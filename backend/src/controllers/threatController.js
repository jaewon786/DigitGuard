const dal = require('../db/dal');
const { notifyGuardianThreat } = require('../services/notificationService');

exports.analyze = async (req, res, next) => {
  try {
    const { text, sourcePackage, userId } = req.body;
    if (!text) {
      return res.status(400).json({ error: '분석할 텍스트가 필요합니다.' });
    }

    const patterns = await dal.getActivePatterns();
    const matched = [];
    let maxLevel = 'none';
    let category = 'unknown';

    for (const p of patterns) {
      try {
        const pat = p.pattern || p.pattern;
        const regex = new RegExp(pat, 'i');
        if (regex.test(text)) {
          const level = p.threatLevel || p.threat_level;
          const cat = p.category;
          matched.push({ pattern: pat, category: cat, level });
          if (level === 'high') {
            maxLevel = 'high';
            category = cat;
          } else if (level === 'medium' && maxLevel !== 'high') {
            maxLevel = 'medium';
            category = cat;
          }
        }
      } catch (_) { }
    }

    const threatLevel = matched.length > 0 ? maxLevel : 'none';
    const riskScore = maxLevel === 'high' ? 95 : maxLevel === 'medium' ? 60 : 10;

    if (matched.length > 0 && userId) {
      await dal.createThreatLog({
        user_id: userId,
        threat_type: category,
        threat_level: maxLevel,
        detected_text: text.substring(0, 200),
        source_package: sourcePackage || '',
        action_taken: maxLevel === 'high' ? 'blocked' : maxLevel === 'medium' ? 'warned' : 'logged',
      });

      if (maxLevel === 'high') {
        notifyGuardianThreat(userId, { threatLevel: maxLevel, category }).catch(() => {});
      }
    }

    res.json({
      threatLevel,
      riskScore,
      category,
      matchedPatterns: matched,
      recommendation: matched.length > 0
        ? '이것은 거짓 광고입니다. 무시하고 뒤로가기를 눌러주세요.'
        : '안전합니다.',
      shouldNotifyGuardian: maxLevel === 'high',
    });
  } catch (err) {
    next(err);
  }
};

exports.logThreats = async (req, res, next) => {
  try {
    const { logs } = req.body;
    if (!Array.isArray(logs)) {
      return res.status(400).json({ error: 'logs 배열이 필요합니다.' });
    }

    const validLogs = logs.filter((log) => log.user_id && log.threat_type && log.threat_level);
    const saved = [];
    for (const log of validLogs) {
      const entry = await dal.createThreatLog({
        user_id: log.user_id,
        threat_type: log.threat_type,
        threat_level: log.threat_level,
        detected_text: log.detected_text,
        source_package: log.source_package,
        action_taken: log.action_taken,
      });
      saved.push(entry);
    }
    res.json({ message: `${saved.length}건의 로그가 저장되었습니다.`, count: saved.length });
  } catch (err) {
    next(err);
  }
};

exports.syncPatterns = async (req, res, next) => {
  try {
    const patterns = await dal.getActivePatterns();
    res.json({
      patterns: patterns.map((p) => ({
        id: p.id,
        pattern: p.pattern,
        patternType: p.patternType || p.pattern_type,
        threatLevel: p.threatLevel || p.threat_level,
        category: p.category,
      })),
      count: patterns.length,
      lastUpdated: new Date().toISOString(),
    });
  } catch (err) {
    next(err);
  }
};

exports.getHistory = async (req, res, next) => {
  try {
    const { userId } = req.params;
    const history = await dal.getThreatHistory(userId);
    res.json({ userId, history, totalCount: history.length });
  } catch (err) {
    next(err);
  }
};
