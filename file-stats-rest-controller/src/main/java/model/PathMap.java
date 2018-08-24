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
	private HashMap<String,ArrayList<DirectoryFile>> folder_to_files = new HashMap<String,ArrayList<DirectoryFile>>();
	private String indexFilePath="C:\\Users\\mayank.patel\\Desktop\\Java Projects\\Assignment_2\\file-stats-rest-controller\\Index";
	private int status=0;
	private int total=0;
	private int current=0;
	private ControllerForFileStats controllerForFileStats= new ControllerForFileStats();
	
	
	PathMap(String path_to_folder){
		path_string= path_to_folder;
		calculateTotal(path_string);
		//		dumpInFile();
	}
	
	PathMap(String path_to_folder, FileStatistics fileStatistics, ControllerForFileStats controllerForFileStats, boolean isSubfolder){
		path_string= path_to_folder;
		if(!isSubfolder)
			calculateTotal(path_string);
		this.fileStatistics = fileStatistics;
		this.controllerForFileStats=controllerForFileStats;
		//		dumpInFile();
	}

	
	private void calculateTotal(String path) {
		File directory = new File(path);
		File[] fList = directory.listFiles();
		for (int i=0;i<fList.length;i++){
			File file = fList[i];
			if (file.isFile()){
				this.total++;
			}
			else{           	
				String new_key=path+"\\\\"+file.getName();
				calculateTotal(new_key);
			}
		}
	}
	//This method traverses through the folders and the subfolders and stores all the files that are present in an ArrayList
	public void store_file_list(String path){
		String key= path;
		if(!folder_to_files.containsKey(key)) {
			folder_to_files.put(key, new ArrayList<DirectoryFile>());
		}
		File directory = new File(path);
		File[] fList = directory.listFiles();
		for (int i=0;i<fList.length;i++){
			File file = fList[i];
			if (file.isFile()){
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= folder_to_files.get(key);
				ret.add(to_be_added);
				folder_to_files.put(key,ret);
				current++;
				status= (int)(current*100)/total;
			//	System.out.println("current="+current+"\t total="+total);
				sendStatus(status);
			}
			else{           	
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= folder_to_files.get(key);
				ret.add(to_be_added);
				folder_to_files.put(key,ret);

				String new_key=path+"\\\\"+file.getName();
				if(!folder_to_files.containsKey(new_key)) {
					folder_to_files.put(new_key, new ArrayList<DirectoryFile>());
					
				}
				store_file_list(new_key);
			}
		}
	}

	public void store_file_list_subfolder(String path){
		String key= path;
		if(!folder_to_files.containsKey(key)) {
			folder_to_files.put(key, new ArrayList<DirectoryFile>());
		}
		File directory = new File(path);
		File[] fList = directory.listFiles();
		for (int i=0;i<fList.length;i++){
			File file = fList[i];
			if (file.isFile()){
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= folder_to_files.get(key);
				ret.add(to_be_added);
				folder_to_files.put(key,ret);
			}
			else{           	
				DirectoryFile to_be_added = new DirectoryFile(file);
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= folder_to_files.get(key);
				ret.add(to_be_added);
				folder_to_files.put(key,ret);

				String new_key=path+"\\\\"+file.getName();
				if(!folder_to_files.containsKey(new_key)) {
					folder_to_files.put(new_key, new ArrayList<DirectoryFile>());
					
				}
				store_file_list_subfolder(new_key);
			}
		}
	}

	
	
	private void sendStatus(int status) {
		this.controllerForFileStats.sendProgress(status);
	}
	public void dumpInFile()
	{
		try {
			ObjectOutputStream objStream1 = new ObjectOutputStream(new FileOutputStream(new File(indexFilePath+"\\indexed.json")));
			objStream1.writeObject(folder_to_files);
			objStream1.close();
		}
		catch(Exception e)
		{
			System.out.println("finally !!");
		}
	}

	public int getStatus() {
		while(status!=100)
			return status;
		return 0;
	}

	//public method that will help the user to get the list of the files present in a given path_string.
	public ArrayList<DirectoryFile> get_files(){

		return this.files;
	}

	public HashMap<String,ArrayList<DirectoryFile>> get_map(){

		return folder_to_files;
	}

	
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






	public void modify_file_list(String name,String path, int kind){
		try {
			if(kind == 0){
				File file= new File(path);
				if(file.isDirectory()) {
					if(kind == 2)
						return;
					folder_to_files.put(path, new ArrayList<DirectoryFile>());
					return;
				}
				DirectoryFile to_be_added= new DirectoryFile(file);
				String pm = path.substring(0, path.indexOf(name));
				files.add(to_be_added);
				ArrayList<DirectoryFile> ret= new ArrayList<DirectoryFile>();
				folder_to_files.put(pm, ret);
			}
			else if(kind == 1){
				File file= new File("");
				file= new File(path);
				if(folder_to_files.containsKey(new PathMap(path))) {
					folder_to_files.remove(new PathMap(path));
					return;
				}
				DirectoryFile to_be_deleted= new DirectoryFile(file);
				String pm= path.substring(0,path.lastIndexOf("\\"));
				for(DirectoryFile fil: files){
					if((fil.get_file_name()+"."+fil.get_type()).equals(name)){
						files.remove(fil);
						//files.add(to_be_deleted);
						break;				
					}
				}
				ArrayList<DirectoryFile> ret = folder_to_files.get(pm);
				ret.remove(to_be_deleted);
				folder_to_files.put(pm, ret);
			}
		}
		catch(Exception e) {
		}
	}

}





