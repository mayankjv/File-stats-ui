package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;




//This class stores the path string and all the files in a given path. A separate class is created for this because the user might need to switch between paths.
class PathMap{
	public String path_string;
	public ArrayList<DirectoryFile> files= new ArrayList<DirectoryFile>();
	HashMap<String,ArrayList<DirectoryFile>> folder_to_files = new HashMap<String,ArrayList<DirectoryFile>>();
	int i;

	PathMap(String path_to_folder){
		path_string= path_to_folder;
		i=0;
		store_file_list(path_string);
	}

	//This method traverses through the folders and the subfolders and stores all the files that are present in an ArrayList
	private void store_file_list(String path){
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
            	store_file_list(new_key);
            }
        }
	}

/*	public void modify_file_list(String name,String path, int kind){
		try {
			if(kind == 0){
				File file= new File(path);
				if(file.isDirectory()) {
					if(kind == 2)
						return;
					folder_to_files.put(new PathMap(path), new ArrayList<DirectoryFile>());
					return;
				}
				DirectoryFile to_be_added= new DirectoryFile(file);
				PathMap pm = new PathMap(path.substring(0, path.indexOf(name)));
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
				PathMap pm= new PathMap(path.substring(0,path.lastIndexOf("\\")));
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
*/

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
}


