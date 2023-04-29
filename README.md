# java_data_manager

Accepts HTTPS on Port 25565
Handles GET, POST requests
"/?key=Dog&password=password"
Uses key and password as parameters, no special characters with exception of Base64 password
Password must be encrypted with public key of server using RSA/ECB/OAEPPadding and sha-1
Uses Content section
Client is responsible for encryption all data to be stored on server
