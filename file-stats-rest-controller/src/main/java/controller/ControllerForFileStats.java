package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import model.DirectoryFile;
import model.FileStatistics;

@RestController
public class ControllerForFileStats{

	ControllerForFileStats(){
		fileStatistics= new FileStatistics();
		noOfPaths=0;
	}
	FileStatistics fileStatistics;
	int noOfPaths;
	
	
	@RequestMapping("/filesInThisFolder")
	public ArrayList<DirectoryFile> getListOfFiles(@RequestParam(value="folder_name") String path) {
		if(noOfPaths==0)
			return null;
		try {
			fileStatistics.execute(path);
		} catch (IOException | InterruptedException e) {
			System.out.println("Exception");
		}
		ArrayList<DirectoryFile> res= fileStatistics.getResult();
		return res;
	}
	@RequestMapping("/addPath")
	public HashSet<String> addAPath(@RequestParam(value="path") String path) {
		File test = new File(path);
		if(!test.exists()) {
			return null;
		}
		path=path.replace("/","\\\\");
		fileStatistics.addNewPath(path);			
		HashSet<String> res= fileStatistics.getAllFolders();
		noOfPaths++;
		return res;
	}

	@RequestMapping("/searchPattern")
	public ArrayList<DirectoryFile> searchPattern(@RequestParam(value="pattern") String pattern, @RequestParam(value="folder")String folderName) {
		ArrayList<DirectoryFile> res=fileStatistics.getSearchResults(pattern,folderName);
		return res;
	}

	
	@RequestMapping("/allFolders")
	public HashSet<String> getListOfPaths() {
		if(noOfPaths==0) {
			return null;
		}
		else {
			return fileStatistics.getAllFolders();
		}
//		fileStatistics.addNewPath(path);			
//		ArrayList<String> res= fileStatistics.getLastAddedFolderName();
//		return res;
	}

}
