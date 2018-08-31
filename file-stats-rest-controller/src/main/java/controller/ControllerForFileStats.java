package controller;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import java.util.*;
import model.DirectoryFile;
import model.FileStatistics;
import model.WatchThread;


@RestController
public class ControllerForFileStats{

	@Autowired
	SimpMessagingTemplate socketResponse;	

	/**
	 * Private variables
	 */
	private FileStatistics fileStatistics;
	private WatchThread th;
	private int noOfPaths;
	private String currentFolder="";
	
	
	/**
	 * constructor
	 */
	public ControllerForFileStats(){
		fileStatistics= new FileStatistics(this);
		noOfPaths=0;
		th= new WatchThread();

	}

	
	/**
	 * this function takes the name of the folder as parameter and returns all files in a given folder
	 * @param path
	 * @return
	 */
	@RequestMapping("/filesInThisFolder")
	public ArrayList<DirectoryFile> getListOfFiles(@RequestParam(value="folder_name") String folderName) {
		if(noOfPaths==0)
			return null;
		try {
			fileStatistics.filesInThisFolder(folderName);
		} catch (IOException | InterruptedException e) {

		}
		ArrayList<DirectoryFile> res= fileStatistics.getResult();
		return res;
	}
	
	
	
	/**
	 * this function is called when a user enters a new path 
	 * @param path
	 * @return
	 */
	@RequestMapping("/addPath")
	public HashSet<String> addAPath(@RequestParam(value="path") String path) {
		th.setPath(path,fileStatistics);
		if(noOfPaths==0)
			th.start();
		File test = new File(path);
		if(!test.exists()) {
			return null;
		}
		path=path.replace("/","\\\\");
		fileStatistics.addNewPath(path);			
		HashSet<String> res= fileStatistics.getAllFolders();
		currentFolder=extractNameFromPath(path);
		noOfPaths++;
		return res;

	}

	
	/**
	 * function to provide search results
	 * @param pattern
	 * @param folderName
	 * @return
	 */
	@RequestMapping("/searchPattern")
	public ArrayList<DirectoryFile> searchPattern(@RequestParam(value="pattern") String pattern, @RequestParam(value="folder")String folderName) {
		ArrayList<DirectoryFile> res=fileStatistics.getSearchResults(pattern,folderName);
		return res;
	}

	/**
	 * funcition to open file in the viewer
	 * @param fileName
	 * @return
	 */
	@RequestMapping("/openFileInViewer")
	public ArrayList<Integer> openFile(@RequestParam(value="fileName") String fileName) {
		fileStatistics.openFile(fileName);
		return null;
	}

	
	/**
	 * function for performing keyword search
	 * @param keyword
	 * @param folderName
	 * @return
	 */
	@RequestMapping("/searchKeyword")
	public ArrayList<DirectoryFile> searchKeyword(@RequestParam(value="keyword") String keyword, @RequestParam(value="folder")String folderName) {
		ArrayList<DirectoryFile> res=fileStatistics.getKeywordSearchResults(keyword,folderName);
		return res;
	}


	
	/**
	 * function that resturns the last added folder to the system
	 * @return
	 */
	@RequestMapping("/lastFolder")
	public String getListOfPaths() {
		if(noOfPaths==0) {
			return null;
		}
		else {
			return currentFolder;
		}
	}
	
	
	/**
	 * this function returns all the tokens of a given file on user request
	 * @param name
	 * @return
	 */
	@RequestMapping("/tokensForAFile")
	public HashMap<String,Integer> getTokens(@RequestParam(value="fileName") String name) {
		return fileStatistics.getTokens(name);
	}

	/**
	 * This function takes a path as parameter and extracts file/folder name from it.
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
	 * this function sends the progress of indexing in order to display progress bar.
	 * @param status
	 */
	public void sendProgress(int status) {
		Gson g = new Gson();
		String jsonString= "{ \"name\" :"+status+"} ";
		socketResponse.convertAndSend("/socketresponse/status", g.toJson(jsonString));
		System.out.println("Response: "+g.toJson(jsonString));
	}


}