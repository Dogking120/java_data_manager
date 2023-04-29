package basic_api;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;

public class ClientHandler implements Runnable {
	
	private SSLSocket socket;
    private DataOutputStream out;
    private InputStream in;
    
    private LocalStorageManager lsm;
	
    public static final Map<String, String> regexMap = new HashMap<>();

    static {
    	regexMap.put("RequestType", "^(GET|POST|PUT|DELETE)");
    	regexMap.put("Key", "key=(\\w*)");
		regexMap.put("Password", "password=([\\w[+/]]*)");
    	regexMap.put("Content", "\"content\":\"(.*?)\"");
    }
    
	public ClientHandler(SSLSocket socket, LocalStorageManager lsm) {
		
		this.socket = socket;
		this.lsm = lsm;
		
	}

	@Override
	public void run() {
		
		System.out.println("Connected from " + this.getSocketAddress());

		try {
			byte[] buf = new byte[4096];
			
	    	in = socket.getInputStream();   	
	    	in.read(buf);
	        
	    	String httpPayload = new String(buf, "UTF-8");
	    	byte[] response = processIn(httpPayload);
	    	
	        String responseHeader = "HTTP/2.0 200 OK\r\n"
	                + "Content-Length: " + response.length + "\r\n"
	                + "Cache-Control: no-cache\r\n"
	                + "Connection: keep-alive\r\n\r\n";
	    	
	        out = new DataOutputStream(
	                new BufferedOutputStream(socket.getOutputStream()));
	        out.writeBytes(responseHeader);
	        out.write(response);
	        out.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
    private byte[] processIn(String payload) throws IOException {
    	
    	final String request_type = getFromPayload(regexMap.get("RequestType"), payload);
    	final String queried_key = getFromPayload(regexMap.get("Key"), payload);
    	final String password = getFromPayload(regexMap.get("Password"), payload);
    	final String content = getFromPayload(regexMap.get("Content"), payload);
    	
    	if (!authenticate(queried_key, password)) {
            String response_text = String.format("Authentication failed!", queried_key);
            
            return response_text.getBytes("UTF-8");
    	}
    	
    	Path file_query_path = this.lsm.query_files(queried_key);
    	
    	System.out.println(payload);
    	
    	switch (request_type) {
    	
    	case "GET":
    		
    		if (file_query_path != null) {
    			
                byte[] data = Files.readAllBytes(file_query_path);
                
        		return data;
    		}
            
            return String.format("Failed read at %s!", queried_key).getBytes("UTF-8");

    	case "POST":
    		    		
    		if (file_query_path != null) {
    			
                byte[] data = content.getBytes("UTF-8");

                lsm.write(file_query_path, data);
                
                return String.format("Sucessfully wrote to %s!", queried_key).getBytes("UTF-8");
                
    		}
    		
    		return String.format("Failed write to %s!", queried_key).getBytes("UTF-8");
    		
    	case "PUT":
    		
    		if (file_query_path == null) {
    			
                byte[] data = content.getBytes("UTF-8");

                lsm.make(Path.of(queried_key), data);
                
                return String.format("Sucessfully made %s!", queried_key).getBytes("UTF-8");
                
    		}
    		
    		return String.format("Failed to make %s!", queried_key).getBytes("UTF-8");
    		
    	default: 
    		return new byte[0];	
    	}
    }
    
    private Boolean authenticate(String key, String encrypted_password) {
    	
    	try {
    		String password = Cryptography.decryptWithPrivateKey(
    				Cryptography.getPrivateKey(Path.of("SSLCertificate.jks"), "password"), 
    				Base64.getDecoder().decode(encrypted_password));
    		
        	UserAuth ua = this.lsm.query_ua(key);
        	Decoder decoder = Base64.getDecoder();
    		
			byte[] hash = Cryptography.PBKDF2Encrypt(password.toCharArray(), decoder.decode(ua.salt));
			
	    	if (Arrays.equals(decoder.decode(ua.hash), hash)) {
	    		
	    		return true;
	    		
	    	}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return false;
    }
    
    private static String getFromPayload(String regex, String payload) {
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(payload);
        if (matcher.find()) {
            // Get the group matched using group() method
            return matcher.group(1);
        }
    	return "";
    }
	
    private String getSocketAddress() {
    	InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
    	String clientIpAddress = socketAddress.getAddress().getHostAddress();
    	
		return clientIpAddress;
    }
	
}
