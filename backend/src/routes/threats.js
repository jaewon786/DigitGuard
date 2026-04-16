const router = require('express').Router();
const controller = require('../controllers/threatController');
const { analyzeLimiter } = require('../middleware/rateLimiter');

router.post('/analyze', analyzeLimiter, controller.analyze);
router.post('/log', controller.logThreats);
router.get('/patterns/sync', controller.syncPatterns);
router.get('/history/:userId', controller.getHistory);

module.exports = router;
