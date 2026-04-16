const router = require('express').Router();
const controller = require('../controllers/appController');

router.post('/check', controller.checkApp);
router.post('/install-request', controller.installRequest);
router.put('/install-request/:id', controller.respondInstallRequest);
router.get('/whitelist/:userId', controller.getWhitelist);

module.exports = router;
