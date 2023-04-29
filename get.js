process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0

const https = require('https')
const crypto = require('crypto')

const pubkey_string = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA2zlNPv9TYNVK88X2RC7VLw7Zi00r8qxOUtx34DxLmBOlglv9TTWTwRx9NAwq+NOYw/f3U4ChNb4hTBIKUHyBcWmK2AuvYoHz1FPQFbNdXdnYOs5HjZ6KNKO1ZWDXLRIe63/MAy7hIu4aLBu+KsYOeFulgdVLVKTGCOr9Zca6CmnRmK/b6heoYUiq+W82q8udfhQRRFm0zVBScwHfFseKkM0DQ4YvOUCt1BxlpqJGV9g3a7T7Iw0SMAhJa0BlslGZJK8ryeYU8JZffa3Nctm4c5Y8Cprk1Fnj5p37gqmvWUWvtJVwMKoqw6r4dPOtr1EghzQg4Rlt78DXR+f1c4n9/iNYZT9g48a/fHXPX4E8e/C95MJlbVs+hIa9nVEQqgB1DAlq+ddzzGAvbKOBttWbIl61fteJ3hCsYZD2XYk6mTc4HiY56z0t+0l3hNKIrqtuumVQ4sCjDaqUQqMisTVs/pOEPqVqejD6gTanr2o9ERnDwZhOBAN7sMNtDP/W/IElEbOltOSV1WIwhV2fMRVFZ4SgagaochAxxt/1ENyPNEiQ1SgOnJjxe0NOWCGspR2U9HyauTUwjs/UhnchHM6uAbalgwy+ERtKsNDVzpMOzuWiyGiXXOH0J3xFpAICZo6CiuaZd6v5aXaQXk8ZEYcRM21GMuvJoUtXSPRLPgUuMQ8CAwEAAQ=="
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