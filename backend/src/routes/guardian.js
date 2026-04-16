const router = require('express').Router();
const controller = require('../controllers/guardianController');

router.post('/link', controller.linkRequest);
router.put('/link/:id/accept', controller.acceptLink);
router.post('/link/accept-by-code', controller.acceptByCode);
router.get('/protected-users', controller.getProtectedUsers);
router.get('/dashboard/:userId', controller.getDashboard);

module.exports = router;
