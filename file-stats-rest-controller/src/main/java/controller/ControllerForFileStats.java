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


	public ControllerForFileStats(){
		fileStatistics= new FileStatistics(this);
		noOfPaths=0;
		th= new WatchThread();

	}


	private FileStatistics fileStatistics;
	private WatchThread th;
	private int noOfPaths;
	private String currentFolder="";

	@RequestMapping("/filesInThisFolder")
	public ArrayList<DirectoryFile> getListOfFiles(@RequestParam(value="folder_name") String path) {
		if(noOfPaths==0)
			return null;
		try {
			fileStatistics.execute(path);
		} catch (IOException | InterruptedException e) {

		}
		ArrayList<DirectoryFile> res= fileStatistics.getResult();
		return res;
	}
	@RequestMapping("/addPath")
	public HashSet<String> addAPath(@RequestParam(value="path") String path) {
		th.set_path(path);
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

	@RequestMapping("/searchPattern")
	public ArrayList<DirectoryFile> searchPattern(@RequestParam(value="pattern") String pattern, @RequestParam(value="folder")String folderName) {
		ArrayList<DirectoryFile> res=fileStatistics.getSearchResults(pattern,folderName);
		return res;
	}

	@RequestMapping("/openFileInViewer")
	public ArrayList<Integer> openFile(@RequestParam(value="fileName") String fileName) {
		fileStatistics.openFile(fileName);
		return null;
	}

	@RequestMapping("/searchKeyword")
	public ArrayList<DirectoryFile> searchKeyword(@RequestParam(value="keyword") String keyword, @RequestParam(value="folder")String folderName) {
		ArrayList<DirectoryFile> res=fileStatistics.getKeywordSearchResults(keyword,folderName);
		return res;
	}


	@RequestMapping("/lastFolder")
	public String getListOfPaths() {
		if(noOfPaths==0) {
			return null;
		}
		else {
			return currentFolder;
		}
	}
	@RequestMapping("/tokensForAFile")
	public HashMap<String,Integer> getTokens(@RequestParam(value="fileName") String name) {
		return fileStatistics.getTokens(name);
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

	public void sendProgress(int status) {
		Gson g = new Gson();
		String jsonString= "{ \"name\" :"+status+"} ";
		socketResponse.convertAndSend("/socketresponse/greetings", g.toJson(jsonString));
	}


}
