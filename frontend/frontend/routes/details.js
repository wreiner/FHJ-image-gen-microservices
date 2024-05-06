var express = require('express');
var router = express.Router();

const axios = require('axios');
const https = require('https');

router.get('/details/:uuid', async function(req, res, next) {
  try {
    const uuid = req.params.uuid;
    //const response = await axios.get(`https://imagegen.wreiner.at/egress/status/${uuid}`);

    const url = "https://imagegen.wreiner.at/egress/status/" + uuid;
    console.log("will fetch: " + url);

    axios.get(url, {
      httpsAgent: new https.Agent({ rejectUnauthorized: false })
    })
    .then(response => {
      console.log(response.data);

      if (response.data.status === "DONE") {
        response.data.image_url = "https://imagegen.wreiner.at/egress/download-image/" + uuid;
      }

      res.render('details', response.data);
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
