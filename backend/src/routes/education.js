const router = require('express').Router();
const controller = require('../controllers/educationController');

router.get('/contents', controller.getContents);
router.get('/contents/:id', controller.getContentById);

module.exports = router;
