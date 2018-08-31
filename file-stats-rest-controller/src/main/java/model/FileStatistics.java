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
	public HashMap<String,PathMap> pathStringMap;
	private ControllerForFileStats controllerForFileStats;

	/**
	 * non-parameterised constructor called when controller object needs not be passed
	 */
	public FileStatistics(){
		path= new HashMap<>();
		pathStringMap= new HashMap<>();
		allPaths= new HashSet<String>();
		curr=0;
	}

	/**
	 * constructor that accepts controller object to send status of progress bar
	 * @param controllerForFileStats
	 */
	public FileStatistics(ControllerForFileStats controllerForFileStats){
		path= new HashMap<>();
		pathStringMap= new HashMap<>();
		allPaths= new HashSet<String>();
		curr=0;
		this.controllerForFileStats=controllerForFileStats;
	}

	/**
	 * method used to add a new path to the system.
	 * @param pathString
	 */
	public void addNewPath(String pathString) {


		PathMap newPath= new PathMap(pathString,this,this.controllerForFileStats,false);
		newPath.addFoldersToMap(pathString);
		path.put(extractNameFromPath(pathString),newPath);
		pathStringMap.put(pathString,newPath);
		if(allPaths.contains(extractNameFromPath(pathString)))
			return;
		String name= extractNameFromPath(pathString);
		allPaths.add(name);
		lastAddedPath=newPath;
		curr++;
	}

	/**
	 * method to add a new subfolder that is present inside a folder
	 * @param pathString
	 */
	public void addNewPathInsideFolder(String pathString) {


		PathMap newPath= new PathMap(pathString,this,this.controllerForFileStats,true);
		newPath.addSubfoldersToMap(pathString);
		path.put(extractNameFromPath(pathString),newPath);
		pathStringMap.put(pathString,newPath);
		if(allPaths.contains(extractNameFromPath(pathString)))
			return;
		String name= extractNameFromPath(pathString);
		allPaths.add(name);
		lastAddedPath=newPath;
		curr++;
	}


	/**
	 * this method returns files inside a folder.
	 * @param folderName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void filesInThisFolder(String folderName) throws IOException, InterruptedException{

		PathMap path_ = path.get(folderName);
		HashMap<String,ArrayList<DirectoryFile>> map =path_.get_map();
		result= map.get(path_.path_string);
		for(DirectoryFile directoryFile: result) {
			if(directoryFile.get_size()==-1) {
				addNewPathInsideFolder(path_.path_string+"\\"+directoryFile.get_file_name());
			}
		}

	}

	/**
	 * function to open a file when a user clicks on it.
	 * @param FileName
	 */
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


	/**
	 * function that returns the value of result for a query
	 * @return
	 */
	public ArrayList<DirectoryFile> getResult(){
		return result;
	}

	/**
	 * function that returns all the folders
	 * @return
	 */
	public HashSet<String> getAllFolders() {
		return allPaths;
	}

	/**
	 * function that takes path as a parameter and returns the name of the file
	 * @param path
	 * @return
	 */
	public String extractNameFromPath(String path) {

		if(path.contains("\\")){
			String res=path.substring(path.lastIndexOf("\\")+1);
			return res;
		}
		else {
			return path;
		}
	}

	/**
	 * method to return file name search results
	 * @param pattern
	 * @param folderName
	 * @return
	 */
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

	/**
	 * method to return keyword search results
	 * @param keyword
	 * @param folderName
	 * @return
	 */
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
	



	/**
	 * method that returns the tokens for a particular file.
	 * @param name
	 * @return
	 */
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

	/**
	 * Method that triggers the change in the primary data structure in case of a watcher event
	 * @param name
	 * @param path_string
	 * @param kind
	 */
	public void updateHashMap(String name, String path_string, int kind){
		try {
			String folderPath= path_string.substring(0, path_string.indexOf(name)-1);
			pathStringMap.get(folderPath).updateMap(name,path_string,kind);
			result=pathStringMap.get(folderPath).get_map().get(pathStringMap.get(folderPath).path_string);

		}
		catch(Exception e) {
			System.out.println("Exception in updateHashMap");
		}
	}

}