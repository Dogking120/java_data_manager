package basic_api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server
{
	final static public String ssl_pass = "password";
	final static public String ssl_cert = "SSLCertificate.jks";
	
	final static public int port = 25565;

    private SSLServerSocket server;
    private SSLSocket socket;
    
    public Server(int port) throws IOException
    {	
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
		try {
			
			SSLServerSocketFactory sslServerSocketFactory = Cryptography.createSSLContext(
					Path.of(ssl_cert), 
					ssl_pass.toCharArray()).getServerSocketFactory();
	    	server = (SSLServerSocket) sslServerSocketFactory.createServerSocket();
	    	server.bind(new InetSocketAddress(port));
	    	
	    	socket = null;
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    public void start() throws IOException {
    	
    	LocalStorageManager lsm = new LocalStorageManager();

    	while (true) {
    		
    		socket = (SSLSocket) server.accept();
       
    		Thread handleThread = new Thread(new ClientHandler(socket, lsm));
    		
    		handleThread.start();
    	}
    }
}