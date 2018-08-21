package model;

/**
 * The task of watching a directory is carried out in a separate thread so that it can always listen to the changes while the user is using the application.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;


// A class which is responsible for continuously monitoring a directory along with all its subdirectories.
//It contains a single istance of WatchService and multiple keys, each of which is assigned to watch a particular folder.
public class WatchThread extends Thread{


	//Declaring an object of WatchService
	WatchService watchService;
	HashMap<Path,WatchKey> map;
	String path = "";
	PathMap p;
	
	//method for getting the parent directory path for registering the directory and the sub directories
	public void set_path(String path_string){
		path= path_string;
	}

	//Run method of the thread that will be executed in parallel with the main thread
	public void run(){
		
		//WatchService.take() method might throw InterruptedExecution exception and IOException is likely to be thrown in this try block.
		try{
				watchService = FileSystems.getDefault().newWatchService(); //instantiating watchService
        		File directory = new File(path);
    			Path _directory = Paths.get(path);

    			WatchKey key;
				registerDirectory(_directory);

    			//fList contains athe list of al the files and folders at a given path.
        		File[] fList = directory.listFiles();
        		//A loop that registers all the subdirectories of the folder with the watchservice so that they can be monitored for changes
        		for (int i=0;i<fList.length;i++){
        			File file = fList[i];
        			//When the current entry in the list is a directory, it needs to be registered with the watchService 
            		if (file.isDirectory()){
						try {
							String new_path= path+"\\"+file.getName();
    						Path _directotyToWatch = Paths.get(new_path);
    						//creating a new key for the newly discovered folder that is not already registered with the watchService
    						key = _directotyToWatch.register(watchService,
                        		  				 StandardWatchEventKinds.ENTRY_CREATE,
                        		   				StandardWatchEventKinds.ENTRY_DELETE,
                        		   				StandardWatchEventKinds.ENTRY_MODIFY);
    						//Inserting the new key in the map
    						map.put(_directotyToWatch,key);
  						} 
  						catch (IOException e) {
    						System.err.println(e);
  						}
            		}
        		}
        		//Infinite loop to keep the Watch Thread always powered up and running.
        		Boolean valid = true;
        		do {
        			//This statement will wait until an event is encoutered.
					WatchKey watchKey = watchService.take();
					for (WatchEvent event : watchKey.pollEvents()) {
						
						WatchEvent.Kind kind = event.kind();
						//When the event is creation of a new Entry
						if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
							String fileName = event.context().toString();
							//Getting the path in order to add the new file to the ArrayList that is maintained in PathMap class
							Path dir= (Path) watchKey.watchable();
							String path_to_be_passed= dir.resolve((Path)event.context()).toString();
							File temp= new File(path_to_be_passed);
							//If the newly added entry is a folder, registering it with the watchService
							if(temp.isDirectory()){
								System.out.println("Folder Created: " + fileName);
								Path new_folder = Paths.get(path_to_be_passed);
								key = new_folder.register(watchService,
                        		  	StandardWatchEventKinds.ENTRY_CREATE,
                        		   	StandardWatchEventKinds.ENTRY_DELETE,
                        		   	StandardWatchEventKinds.ENTRY_MODIFY);
							}
							//If the newly added entry is a file, adding it to the ArrayList
							else{
								System.out.println("File Created:" + fileName);
								FileStatistics fileStatistics= new FileStatistics();
								fileStatistics.call_for_change(fileName,path_to_be_passed,0);
							}
						}
						//When the new entry is a Delete event
						else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
							String fileName = event.context().toString();
							//Getting the path.
							Path dir= (Path) watchKey.watchable();
							String path_to_be_passed= dir.resolve((Path)event.context()).toString();
							File temp= new File(path_to_be_passed);
							//If the entry that was deleted in a folder, simply display a a message
							if(temp.isDirectory()){
								System.out.println("Folder deleted:" + fileName);
							}
							//if the deleted entry is a file, display a message and delete the entry from the ArrayList
							else{
								System.out.println("File Deleted:" + fileName);
								FileStatistics fileStatistics= new FileStatistics();
								fileStatistics.call_for_change(fileName,path_to_be_passed,0);
							}
							
						}
						//When the event is a Modification
						else{
//							System.out.println("Modify!");
							String fileName = event.context().toString();
							//clear_screen();
							Path dir= (Path) watchKey.watchable();
							String path_to_be_passed= dir.resolve((Path)event.context()).toString();
							FileStatistics fileStatistics= new FileStatistics();
							fileStatistics.call_for_change(fileName,path_to_be_passed,0);
						}

					}
					valid= watchKey.reset();
					//when a folder is deleted, the corresponding watchkey is deleted
					if(!valid) map.remove(watchKey);
            	} while(!map.isEmpty());
        	}
		catch(Exception e){
			System.out.println("Exception");
		}
	}

	private void registerDirectory(Path _directory) throws IOException {
		//Registering the parent directory with watchService so as to monitor it.
		WatchKey key = _directory.register(watchService,
		        		  	StandardWatchEventKinds.ENTRY_CREATE,
		        		   	StandardWatchEventKinds.ENTRY_DELETE,
		        		   	StandardWatchEventKinds.ENTRY_MODIFY);
		//instantiating the HashaMap and putting the key created above in the map
		map= new HashMap<Path,WatchKey>();
		map.put(_directory,key);
	}

}



