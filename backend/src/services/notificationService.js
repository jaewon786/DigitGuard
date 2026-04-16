const { getFirebaseAdmin } = require('./firebase');
const store = require('../db/inMemoryStore');

// FCM 푸시 알림 전송
async function sendPushNotification(fcmToken, title, body, data = {}) {
  const admin = getFirebaseAdmin();

  if (!admin) {
    console.log('[개발모드] 푸시 알림 시뮬레이션:', { title, body, data });
    return { success: true, simulated: true };
  }

  const message = {
    token: fcmToken,
    notification: { title, body },
    data: Object.fromEntries(
      Object.entries(data).map(([k, v]) => [k, String(v)])
    ),
    android: {
      priority: 'high',
      notification: {
        channelId: 'digitguard_guardian',
        sound: 'default',
      },
    },
  };

  const response = await admin.messaging().send(message);
  return { success: true, messageId: response };
}

// 보호자에게 위험 감지 알림
async function notifyGuardianThreat(protectedUserId, threatInfo) {
  const links = store.guardianLinks.filter(
    (l) => l.protected_id === protectedUserId && l.status === 'active'
  );

  const results = [];
  for (const link of links) {
    const guardian = store.findUserById(link.guardian_id);
    if (!guardian) continue;

    const result = await sendPushNotification(
      guardian.fcmToken || 'simulated-token',
      '위험 감지',
      `피보호자에게 ${threatInfo.category} 위험이 감지되었습니다.`,
      {
        type: 'threat_detected',
        protectedUserId,
        threatLevel: threatInfo.threatLevel,
        category: threatInfo.category,
      }
    );
    results.push({ guardianId: guardian.id, guardianName: guardian.name, ...result });
  }

  return results;
}

// 보호자에게 앱 설치 승인 요청 알림
async function notifyGuardianInstallRequest(protectedUserId, appInfo) {
  const links = store.guardianLinks.filter(
    (l) => l.protected_id === protectedUserId && l.status === 'active'
  );

  const results = [];
  for (const link of links) {
    const guardian = store.findUserById(link.guardian_id);
    if (!guardian) continue;

    const result = await sendPushNotification(
      guardian.fcmToken || 'simulated-token',
      '앱 설치 승인 요청',
      `${appInfo.appName} 앱 설치 승인을 요청합니다.`,
      {
        type: 'install_request',
        protectedUserId,
        packageName: appInfo.packageName,
        appName: appInfo.appName,
        requestId: appInfo.requestId,
      }
    );
    results.push({ guardianId: guardian.id, ...result });
  }

  return results;
}

module.exports = {
  sendPushNotification,
  notifyGuardianThreat,
  notifyGuardianInstallRequest,
};
