var express = require('express');
var router = express.Router();

const axios = require('axios');
const https = require('https');

router.get('/list', async function(req, res, next) {
  try {
    const uuid = req.params.uuid;

    const url = "https://" + IMAGE_GEN_API_URL + "/egress/get_all_generation_requests";
    console.log("will fetch: " + url);

    axios.get(url, {
      httpsAgent: new https.Agent({ rejectUnauthorized: false })
    })
    .then(response => {
      console.log(response.data);

      // Check if the 'gallery' query parameter exists
      if (req.query.gallery) {
        // Render a different EJS template based on the presence of 'gallery' parameter
        res.render('galleryList', { requests: response.data });
      } else {
        res.render('list', { requests: response.data });
      }
    })
    .catch(error => {
      console.error(error);
      next(error);
    });
  } catch (error) {
    next(error);
  }
});

module.exports = router;
