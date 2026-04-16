const router = require('express').Router();
const controller = require('../controllers/urlController');

router.post('/check', controller.checkUrl);

module.exports = router;
