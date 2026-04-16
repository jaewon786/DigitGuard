const router = require('express').Router();
const controller = require('../controllers/authController');
const { authLimiter } = require('../middleware/rateLimiter');

router.post('/register', authLimiter, controller.register);
router.post('/login', authLimiter, controller.login);
router.delete('/account', controller.deleteAccount);

module.exports = router;
