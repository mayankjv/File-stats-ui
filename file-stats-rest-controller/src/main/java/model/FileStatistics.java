package model;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import controller.ControllerForFileStats;

import java.awt.Desktop;
import java.io.*;

/**
 * @author mayank.patel
 * This program takes the path to a directory(folder) as an input from the user and monitors the directory continuously for changes.
 * Apart from monitoring, it also allows user to list all the files present in that directory as well as the subdirectories.
 * The listing can be done in sorted manner or in random manner according to user requirements.
 * The user can also perform a search based on attributes of the files like Name and size .
 * The only public class is names FileStatistics which contains the main method and is responsible for allowing user to interact with the application.
 * 
 * 
 */
//The public class that contains the main method, the interaction with user will be done in this class.


@Service
public class FileStatistics{



	private HashSet<String> allPaths;
	private HashMap<String,PathMap> path;
	private ArrayList<DirectoryFile> result;
	private int curr;
	private PathMap lastAddedPath;
	public int status=0;
	private ControllerForFileStats controllerForFileStats;


	public FileStatistics(){
		path= new HashMap<>();
		allPaths= new HashSet<String>();
		curr=0;
	}

	public FileStatistics(ControllerForFileStats controllerForFileStats){
		path= new HashMap<>();
		allPaths= new HashSet<String>();
		curr=0;
		this.controllerForFileStats=controllerForFileStats;
	}
	public void addNewPath(String pathString) {
		
		
		PathMap newPath= new PathMap(pathString,this,this.controllerForFileStats,false);
		newPath.store_file_list(pathString);
		path.put(extractNameFromPath(pathString),newPath);
		if(allPaths.contains(extractNameFromPath(pathString)))
			return;
		String name= extractNameFromPath(pathString);
		allPaths.add(name);
		lastAddedPath=newPath;
		curr++;
	}
	

	public void addNewPathInsideFolder(String pathString) {
		
		
		PathMap newPath= new PathMap(pathString,this,this.controllerForFileStats,true);
		newPath.store_file_list_subfolder(pathString);
		path.put(extractNameFromPath(pathString),newPath);
		if(allPaths.contains(extractNameFromPath(pathString)))
			return;
		String name= extractNameFromPath(pathString);
		allPaths.add(name);
		lastAddedPath=newPath;
		curr++;
	}
	
	
	//Main method 
	public void execute(String folderName) throws IOException, InterruptedException{

		PathMap path_ = path.get(folderName);
		HashMap<String,ArrayList<DirectoryFile>> map =path_.get_map();
		result= map.get(path_.path_string);
		//			result = path_.get_files();
		for(DirectoryFile directoryFile: result) {
			if(directoryFile.get_size()==-1) {
				addNewPathInsideFolder(path_.path_string+"\\\\"+directoryFile.get_file_name());
			}
		}

	}
	public void openFile(String FileName) {
		for(DirectoryFile directoryFile: result) {
			if(directoryFile.get_file_name().equals(FileName)) {
				try {
					Desktop.getDesktop().open(directoryFile.get_file());
					System.out.println("Words After: "+directoryFile.get_words());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<DirectoryFile> getResult(){
		return result;
	}
	public HashSet<String> getAllFolders() {
		return allPaths;
	}
	public String extractNameFromPath(String path) {

		if(path.contains("\\")){
			String res=path.substring(path.lastIndexOf("\\")+1);
			return res;
		}
		else {
			return path;
		}
	}

	public ArrayList<DirectoryFile> getSearchResults(String pattern, String folderName) {
		PathMap path_= path.get(folderName);
		ArrayList<DirectoryFile> search_results = new ArrayList<DirectoryFile>();
		ArrayList<DirectoryFile> temp= path_.get_files();				
		KMPSearch obj = new KMPSearch();
		for(int i=0;i<temp.size();i++){
			int[] arr= obj.kmp(temp.get(i).get_file_name().toCharArray(), pattern.toCharArray());
			if(arr.length!=0){
				search_results.add(temp.get(i));
			}
		}		
		return search_results;

	}

	public ArrayList<DirectoryFile> getKeywordSearchResults(String keyword, String folderName) {
		PathMap path_= path.get(folderName);
		ArrayList<DirectoryFile> search_results = new ArrayList<DirectoryFile>();
		ArrayList<DirectoryFile> temp= path_.get_files();	
		KeywordCountComparator keywordCountComparator = new KeywordCountComparator();
		keywordCountComparator.set_keyword(keyword);
		for(int i=0;i<temp.size();i++){
			if(temp.get(i).getTokens().containsKey(keyword)){
				search_results.add(temp.get(i));
			}
		}	
		Collections.sort(search_results, keywordCountComparator);
		return search_results;

	}	

	public HashMap<String, Integer> getTokens(String name) {
		for(String path_string: path.keySet()) {
			PathMap current= path.get(path_string);
			ArrayList<DirectoryFile> al= current.get_files();
			for(DirectoryFile file: al) {
				if(file.get_file_name().equals(name)) {
					return file.getTokens();
				}
			}
		}
		return null;
	}

	public void call_for_change(String name, String path_string, int kind){
		try {
			path.get(path_string).modify_file_list(name,path_string,kind);
		}
		catch(Exception e) {
			System.out.println("Exception in call_for_change");
		}
	}

}
