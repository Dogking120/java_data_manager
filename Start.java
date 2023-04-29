package basic_api;

import java.io.IOException;

public class Start {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Starting API Server...");
		
		Server server = new Server(25565);
		server.start();
		
	}
}