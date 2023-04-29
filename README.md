# java_data_manager

Accepts HTTPS on Port 25565 <br />
Handles GET, POST requests <br />
Example: "/?key=Dog&password=password" <br />
Uses key and password as parameters, no special characters with exception of Base64 password <br />
Password must be encrypted with public key of server using RSA/ECB/OAEPPadding and sha-1 <br />
Uses Content section <br />
Client is responsible for encryption all data to be stored on server <br />
