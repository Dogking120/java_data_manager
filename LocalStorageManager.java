package basic_api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LocalStorageManager {

	public static Path HOME_DIR = Paths.get(System.getProperty("user.dir"));
	private static final String FOLDER_NAME = "localStorage";
	private static final String HASH_FOLDER_NAME = "hashes";
	private static final String HASH_FILE = "hashes";
	
	private ArrayList<Path> file_array;
	private ArrayList<UserAuth> ua_array;
	
	private void getFilesDir(Path dir) throws IOException {
		
	    if (!Files.exists(dir) && !Files.isDirectory(dir)) {
	    	
	    	Files.createDirectory(dir);
	    	
	    }
		
	    DirectoryStream.Filter<Path> FILE_FILTER = new DirectoryStream.Filter<Path>() {
	        @Override
	        public boolean accept(Path file) throws IOException {
	            return (!Files.isDirectory(file));
	        }
	    };
		
		DirectoryStream<Path> stream = Files.newDirectoryStream(dir, FILE_FILTER);
		
        for (Path path : stream) {
        	
        	this.file_array.add(path);
      
        }
		
	}
	
	private void getHashList(Path dir) throws IOException {
		
		Path hash_file_path = dir.resolve(HASH_FILE);
	    if (Files.exists(hash_file_path) && Files.isDirectory(dir)) {
	    	
			List<String> strings = Files.readAllLines(hash_file_path);
			
			UserAuth ua = new UserAuth();
			
			for (String string : strings) {
				
				if (ua.key == null) {
					ua.key = string;
				} else if (ua.hash == null) {
					ua.hash = string;
				} else if (ua.salt == null) {
					ua.salt = string;
					ua_array.add(ua);
					ua = new UserAuth();
				}
			}
	    	
	    } else {
	    	
	    	Files.createDirectory(dir);
	    	
	    	Files.createFile(hash_file_path);
	    	
	    }
		
	}
	
	public LocalStorageManager() throws IOException {
	    	
	    Path folder_path = Path.of(FOLDER_NAME);
	    Path hash_path = folder_path.resolve(HASH_FOLDER_NAME);
	    
	    file_array = new ArrayList<Path>();
	    ua_array = new ArrayList<UserAuth>();
	    
	    getFilesDir(folder_path);
	    
	    getHashList(hash_path);
	    
	}
	
	public Path query_files(String key) {
		
	    for (Path file : file_array) {
	    	
	    	String file_string = file.getFileName().toString();
	    	
	    	if (file_string.equals(key)) {
	    		return file;
	    	}
	    	
	    }
		return null;
		
	}
	
	public UserAuth query_ua(String key) {
		
	    for (UserAuth ua : ua_array) {
	    	
	    	String ua_key = ua.key;
	    	
	    	if (ua_key.equals(key)) {
	    		return ua;
	    	}
	    	
	    }
		return null;
		
	}
	
	public void make(Path file, byte[] buffer) throws IOException {
		
		Path folder_path = Path.of(FOLDER_NAME);
		Path path = folder_path.resolve(file);
		
		try {
			Path createdFilePath = Files.createFile(path);
			System.out.println("File Created at Path : "+createdFilePath);
			
			this.file_array.add(createdFilePath);
			this.write(createdFilePath, buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void write(Path file, byte[] buffer) throws IOException {
		
		Lock lock = new ReentrantLock(); 
		lock.lock();
		try {
			FileOutputStream out = new FileOutputStream(file.toFile());
			
	        out.write(buffer);
	        out.flush();
	        out.close();
		}
		catch (Exception e) {
			return;
		}
		finally {
			lock.unlock();
		}
	}
	
}