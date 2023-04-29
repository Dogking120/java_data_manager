process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0

const https = require('https')
const crypto = require('crypto')

const pubkey_string = "certificate pubkey that you generate on the server for TLS connections, in raw Base64 format"
const pubkey = crypto.createPublicKey({
    key: pubkey_string,
    format: 'der',
    type: 'spki',
    encoding: 'base64'
})

let encryptedPassword = crypto.publicEncrypt({
    key: pubkey,
    padding: crypto.constants.RSA_PKCS1_OAEP_PADDING,
    encoding: 'utf-8'
}, "password").toString('base64')

const options = {
    url: 'https:/localhost',
    path: '/?key=Dog&password=' + encryptedPassword,
    port: '25565',
    method: 'GET'
};

const req = https.request(options, (res) => {
    /*
    const cert = res.connection.getPeerCertificate()
    const pubkey = crypto.createPublicKey({
        key: cert.pubkey,
        format: 'der',
        type: 'spki'
    })
    */

    let data = ''
     
    res.on('data', (chunk) => {
        data += chunk;
    });
    
    res.on('end', () => {
        console.log(data)
    });
       
})

req.on('error', (err) => {
    console.log("Error: ", err)
})

req.end()
