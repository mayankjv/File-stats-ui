package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import controller.ControllerForFileStats;


//This class stores the path string and all the files in a given path. A separate class is created for this because the user might need to switch between paths.
public class PathMap{

	public String path_string;
	private FileStatistics fileStatistics;
	private ArrayList<DirectoryFile> files= new ArrayList<DirectoryFile>();
	private HashMap<String,ArrayList<DirectoryFile>> filesInsideFolders = new HashMap<String,ArrayList<DirectoryFile>>();
	private String indexFilePath="C:\\Users\\mayank.patel\\Desktop\\Java Projects\\Assignment_2\\file-stats-rest-controller\\Index";
	private int status=0;
	private int total=0;
	private int current=0;
	private ControllerForFileStats controllerForFileStats= new ControllerForFileStats();

	/**
	 * Parameterized constructor to PathMap class
	 * @param path_to_folder
	 */
	PathMap(String path_to_folder){
		path_string= path_to_folder;
	}

	/**
	 * over loaded parameterized constructor to PathMap
	 * @param path_to_folder
	 * @param fileStatistics
	 * @param controllerForFileStats
	 * @param isSubfolder
	 */
	PathMap(String path_to_folder, FileStatistics fileStatistics, ControllerForFileStats controllerForFileStats, boolean isSubfolder){
		path_string= path_to_folder;
		if(!isSubfolder)
			calculateTotalNumberOfFiles(path_string);
		this.fileStatistics = fileStatistics;
		this.controllerForFileStats=controllerForFileStats;
		//		dumpInFile();
	}

	/**
	 * This function calculates total number of files present inside a folder and in the sub-folders.
	 * @param path
	 */
	private void calculateTotalNumberOfFiles(String path) {
		File directory = new File(path);
		File[] fList = directory.listFiles();
		for (int i=0;i<fList.length;i++){
			File file = fList[i];
			if (file.isFile()){
				this.total++;
			}
			else{           	
				String new_key=path+"\\"+file.getName();
				calculateTotalNumberOfFiles(new_key);
			}
		}
	}


	/**
	 * This method traverses through the folders and the subfolders and stores all the files that are present in a HashMap
	 * @param path
	 */
	public void addFoldersToMap(String path){
		String key= path;
		if(!filesInsideFolders.containsKey(key)) {
			filesInsideFolders.put(key, new ArrayList<DirectoryFile>());
		}
		File directory = new File(path);
		File[] fList = directory.listFiles();
		for (int i=0;i<fList.length;i++){
			File file = fList[i];
			if (file.isFile()){
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= filesInsideFolders.get(key);
				ret.add(to_be_added);
				filesInsideFolders.put(key,ret);
				current++;
				status= (int)(current*100)/total;
				//	System.out.println("current="+current+"\t total="+total);
				sendStatus(status);
			}
			else{           	
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= filesInsideFolders.get(key);
				ret.add(to_be_added);
				filesInsideFolders.put(key,ret);

				String new_key=path+"\\"+file.getName();
				if(!filesInsideFolders.containsKey(new_key)) {
					filesInsideFolders.put(new_key, new ArrayList<DirectoryFile>());

				}
				addFoldersToMap(new_key);
			}
		}
	}


	/**
	 * This function carries out the same functionality as the above, for sub folders.
	 * @param path
	 */
	public void addSubfoldersToMap(String path){
		String keyToMap = path;
		if(!filesInsideFolders.containsKey(keyToMap)) {
			filesInsideFolders.put(keyToMap, new ArrayList<DirectoryFile>());
		}
		File currentDirectory = new File(path);
		File[] fList = currentDirectory.listFiles();
		for (int i=0;i<fList.length;i++){
			File file = fList[i];
			if (file.isFile()){
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= filesInsideFolders.get(keyToMap);
				ret.add(to_be_added);
				filesInsideFolders.put(keyToMap,ret);
			}
			else{     

				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= filesInsideFolders.get(keyToMap);
				ret.add(to_be_added);
				filesInsideFolders.put(keyToMap,ret);
				String new_key=path+"\\"+file.getName();				

				if(!filesInsideFolders.containsKey(new_key)) {

					filesInsideFolders.put(new_key, new ArrayList<DirectoryFile>());

				}
				addSubfoldersToMap(new_key);

			}
		}
	}


	/**
	 * function that sends the status of indexing to frontend via Web Socket
	 * @param status
	 */
	private void sendStatus(int status) {
		this.controllerForFileStats.sendProgress(status);
		System.out.println(status);
	}

	/**
	 * function used to dump the data of primary data structure (HashMap) in json file
	 */
	public void dumpInFile()
	{
		try {
			ObjectOutputStream objStream1 = new ObjectOutputStream(new FileOutputStream(new File(indexFilePath+"\\indexed.json")));
			objStream1.writeObject(filesInsideFolders);
			objStream1.close();
		}
		catch(Exception e)
		{
			System.out.println("finally !!");
		}
	}


	//public method that will help the user to get the list of the files present in a given path_string.
	public ArrayList<DirectoryFile> get_files(){
		return this.files;
	}

	/**
	 * function that returns the value of HashMap to the controller
	 * @return
	 */
	public HashMap<String,ArrayList<DirectoryFile>> get_map(){
		return filesInsideFolders;
	}

	/**
	 * Overridden Equals method, that compares two PathMaps based on their Path strings.
	 */
	@Override
	public boolean equals(Object p) {
		if(p==this)
			return true;
		PathMap p1= (PathMap)p;
		return p1.path_string.equals(this.path_string);
	}


	@Override
	public int hashCode()
	{ 
		return (int)(new DirectoryFile(new File(this.path_string))).get_last_modified();
	}


	/**
	 * This method updates the value of the Map when a watcher event is encountered
	 * @param name
	 * @param path
	 * @param typeOfEvent
	 */
	public void updateMap(String name,String path, int typeOfEvent){
		try {
			if(typeOfEvent == 0){
				File file= new File(path);
				if(file.isDirectory()) {
					filesInsideFolders.put(path, new ArrayList<DirectoryFile>());
					ArrayList<DirectoryFile> ret = filesInsideFolders.get(path.substring(0,path.indexOf(name)-1));
					System.out.println("Key: "+path.substring(0,path.indexOf(name)-1));
					for(DirectoryFile f: ret)
						System.out.println(f.get_file_name());

					ret.add(new DirectoryFile(file));

					for(DirectoryFile f: ret)
						System.out.println(f.get_file_name());

					filesInsideFolders.put(path.substring(0,path.indexOf(name)-1),ret);
					return;
				}
				DirectoryFile to_be_added= new DirectoryFile(file);
				String pm = path.substring(0, path.indexOf(name)-1);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret;
				if(filesInsideFolders.containsKey(pm)) {
					ret= filesInsideFolders.get(pm);
					ret.add(to_be_added);
				}
				else
					ret= new ArrayList<DirectoryFile>();
				filesInsideFolders.put(pm, ret);
			}
			else if(typeOfEvent == 12){
				File file= new File(path);
				String pm= path.substring(0,path.indexOf(name)-1);
				//				for(DirectoryFile fil: files){
				//					if((fil.get_file_name()+"."+fil.get_type()).equals(name)){
				//						files.remove(fil);
				//						break;				
				//					}
				//				}
				ArrayList<DirectoryFile> ret = filesInsideFolders.get(pm);
				for(DirectoryFile current : ret) {
					if(current.get_file_name().equals(name.substring(0, name.indexOf('.'))))
						ret.remove(current);
				}
				filesInsideFolders.put(pm, ret);
			}
			else if(typeOfEvent == 11) {
				ArrayList<DirectoryFile> temp = filesInsideFolders.get(path);
				deleteFolder(temp,path,name);
				String pm= path.substring(0,path.indexOf(name)-1);			
				ArrayList<DirectoryFile> ret = filesInsideFolders.get(pm);
				for(DirectoryFile current : ret) {
					if(current.get_file_name().equals(name))
						ret.remove(current);
				}

			}
		}
		catch(Exception e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * this funstion deleted all the subfolders and files from the map when a folder is deleted from memory.
	 * @param temp
	 * @param path
	 * @param name
	 */
	private void deleteFolder(ArrayList<DirectoryFile> temp, String path, String name) {
		System.out.println("Delete Folder: "+name+" Path: "+path);
		for(DirectoryFile f :temp ) {
			if(f.get_type().equals("Folder")) {
				String newPath=path.substring(0, path.indexOf(name)-1);
				deleteFolder(filesInsideFolders.get(newPath),newPath,newPath.substring(newPath.lastIndexOf("//")+1));
			}
		}
		System.out.println("Deleted Folder: "+name+" Path: "+path);
	}


	/**
	 * this funstion deleted all the subfolders and files from the map when a folder is deleted from memory.
	 * @param temp
	 * @param path
	 * @param name
	 */
	private void deleteFolder(ArrayList<DirectoryFile> temp, String path, String name) {
		System.out.println("Delete Folder: "+name+" Path: "+path);
		for(DirectoryFile f :temp ) {
			if(f.get_type().equals("Folder")) {
				String newPath=path.substring(0, path.indexOf(name)-1);
				deleteFolder(filesInsideFolders.get(newPath),newPath,newPath.substring(newPath.lastIndexOf("//")+1));
			}
		}
		System.out.println("Deleted Folder: "+name+" Path: "+path);
	}

}
