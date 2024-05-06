var express = require('express');
var router = express.Router();

const axios = require('axios');
const https = require('https');

router.post('/submit', async (req, res) => {
    try {
        const prompt = req.body.prompt;

        const url = "https://imagegen.wreiner.at/createGenerateRequest";;

        // Create an instance of https.Agent to accept self-signed certificates
        const agent = new https.Agent({ rejectUnauthorized: false });

        // Send POST request to webservice with the custom https agent
        const response = await axios.post(url, { prompt }, { httpsAgent: agent });

        // Extract UUID from response
        const uuid = response.data.uuid;

        // Delay for 2 seconds
        await new Promise(resolve => setTimeout(resolve, 2000));

        // Redirect to /details route with UUID
        res.redirect(`/details/${uuid}`);
    } catch (error) {
        console.error(error);
        res.status(500).send('Internal Server Error');
    }
});

module.exports = router;
