# java_data_manager

Accepts HTTPS on Port 25565\n
Handles GET, POST requests\n
Example: "/?key=Dog&password=password"\n
Uses key and password as parameters, no special characters with exception of Base64 password\n
Password must be encrypted with public key of server using RSA/ECB/OAEPPadding and sha-1\n
Uses Content section\n
Client is responsible for encryption all data to be stored on server\n
