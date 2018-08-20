package model;

import java.util.*;
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
public class FileStatistics{

	HashSet<String> allPaths;
	HashMap<String,PathMap> path;
	private int mainMenu;
	private int choice;
	String searchString;
	int valueSearch;
	ArrayList<DirectoryFile> result;
	int curr;
	
	public FileStatistics(){
		path= new HashMap<>();
		allPaths= new HashSet<String>();
		curr=0;
		mainMenu=1;
		choice=-1;
	}
	public void addNewPath(String pathString) {
		PathMap newPath= new PathMap(pathString);
		path.put(extractNameFromPath(pathString),newPath);
		if(allPaths.contains(extractNameFromPath(pathString)))
			return;
		String name= extractNameFromPath(pathString);
		allPaths.add(name);
		System.out.println("Path Added: "+pathString);
		System.out.println(allPaths.size());
		curr++;
	}
	
	//Main method 
	public void execute(String folderName) throws IOException, InterruptedException{
		
			PathMap path_ = path.get(folderName);
			HashMap<String,ArrayList<DirectoryFile>> map =path_.get_map();
			if(mainMenu == 1) {
				result= map.get(path_.path_string);
//				result = path_.get_files();
				for(DirectoryFile directoryFile: result) {
					if(directoryFile.get_size()==-1) {
						addNewPath(path_.path_string+"\\\\"+directoryFile.get_file_name());
					}
				}
			}
	}
/*			
			//Condition when a user wants the files listed in a sorted manner.
			else if(mainMenu == 2){
				if(choice ==1) {
					ArrayList<DirectoryFile> temp = new ArrayList<>();
					Collections.sort(temp, new NameComparatorAsc());
					result = temp;
				}
				else if(choice == 2) {
					ArrayList<DirectoryFile> temp = new ArrayList<>();
					Collections.sort(temp, new LastModifiedComparatorAsc());
					result = temp;					
				}
				else {
					ArrayList<DirectoryFile> temp = new ArrayList<>();
					Collections.sort(temp, new SizeComparatorAsc());
					result = temp;
				}
			}
			
*/
			
/*			//When the user wants to search for a file
			else if(choice == 3){
				ArrayList<DirectoryFile> search_results = new ArrayList<DirectoryFile>();
				ArrayList<DirectoryFile> temp= path_.get_files();				
					KMPSearch obj = new KMPSearch();
					for(int i=0;i<temp.size();i++){
						int[] arr= obj.kmp(temp.get(i).get_file_name().toCharArray(), searchString.toCharArray());
						if(arr.length!=0){
							search_results.add(temp.get(i));
						}
					}		
					result = search_results;
			}
			else
				result = new ArrayList<DirectoryFile>();
*/


 
 	//Function that clears screen and then displays message to assist user to go to the main menu
/* 	private static void displayNavigationMessage() throws IOException, InterruptedException{
 		clear_screen();
		System.out.println("PRESS ENTER TO GO TO THE MAIN MENU \n\n\n\n");
 	}
*/


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
 	
 	
	public FileStatistics(int mainMenu, int choice){
		mainMenu=this.mainMenu;
		choice=this.choice;		
	}
	public FileStatistics(int mainMenu, int choice, String searchString){
		mainMenu=this.mainMenu;
		choice=this.choice;		
		searchString=this.searchString;
		valueSearch=-1;
	}
	public FileStatistics(int mainMenu, int choice, int value){
		mainMenu=this.mainMenu;
		choice=this.choice;		
		searchString=this.searchString;
		valueSearch= value;
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
 
}
