const config = require('../config');

let adminInstance = null;

function getFirebaseAdmin() {
  if (adminInstance) return adminInstance;
  if (!config.firebase.projectId) return null;

  try {
    const admin = require('firebase-admin');
    if (!admin.apps.length) {
      admin.initializeApp({
        credential: admin.credential.cert({
          projectId: config.firebase.projectId,
          privateKey: config.firebase.privateKey,
          clientEmail: config.firebase.clientEmail,
        }),
      });
    }
    adminInstance = admin;
    return admin;
  } catch (err) {
    console.warn('Firebase Admin 초기화 실패:', err.message);
    return null;
  }
}

module.exports = { getFirebaseAdmin };
