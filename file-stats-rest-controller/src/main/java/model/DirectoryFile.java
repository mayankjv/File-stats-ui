package model;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

//This class stores all the attributes of a single file along with all the member functions that are needed to calculate the values of the attributes.
public class DirectoryFile{

	private File file;
	private String type;
	private String name;
	private int lines;
	private long words;
	private long size;
	private long last_modified; 
	private HashMap<String,Integer> tokens= new HashMap<String,Integer>();

	//A non-parameterised constructor used to instantiate an object that is used to invoke functions in other classes
	DirectoryFile(){
		
	}
	//A parameterised Constructor that sets all the attributes of a file by calling suitable functions.
	DirectoryFile(File f){
		file = f;
		if(!file.isDirectory()) {
			try{
				type = set_type(file);
				name = set_name(file);
				set_lines();
				set_words();
				set_size();
				set_last_modified();
			}
			catch(IOException e){
				//System.out.println("IO Exception !");
			}

		}
		//When the file is a folder
		else {
			type="Folder";
			name=file.getName();
			lines=-1;
			words=-1;
			size=-1;
			last_modified=-1;
		}
		//Since BufferedReader is used, it might throw IOException

	}
	
	//Getter Method for getting the File object associated with an
	public File get_file() {
		return file;
	}
	
	//Getter mehtod for Name of the file
	public String get_file_name(){
		return name;
	}
	//Getter method for file type
	public String get_type(){
		return type;
	}
	//Getter method for Number of lines in the file
	public int get_lines(){
		return lines;
	}
	//Getter method for number of words in the file
	public long get_words(){
		return words;
	}
	//Getter method for tokens in the file
	public HashMap<String,Integer> get_tokens(){
		return tokens;
	}
	////Getter method for the size of the file
	public long get_size(){
		return size;
	}
	//Getter method for the last modified timestamp of the file
	public long get_last_modified(){
		return last_modified;
	}
	//Method to set last modified timestamp
	private void set_last_modified(){

		last_modified=file.lastModified();
	
	}
	//Method to set the size of the file
	private void set_size(){
	
		size= file.length();
	
	}
	
	//Method to set the number of words int the file
	private void set_words() throws IOException{
	
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String temp="";
		String[] words_;
		while(temp != null){
			try{
				temp= reader.readLine();
				words_= temp.split("\\s+");
				words+= words_.length;
				for(String word : words_) {
					if(tokens.containsKey(word)) {
						int curr= tokens.get(word);
						tokens.put(word,curr+1 );
					}
					else
						tokens.put(word, 1);
				}
			}
			catch(NullPointerException e){
//				System.out.println("File Empty!");
			}
		}
		reader.close();
	
	}
	//Method to set the number of lines in the file
	private void set_lines() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while (reader.readLine() != null) lines++;
		reader.close();
	}
	////Method to set the Name of the file
	private String set_name(File file){

        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
        	return fileName.substring(0,fileName.lastIndexOf("."));

        }
        else return "";

	}
	////Method to set the file type
	private String set_type(File file){
	
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
        	return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else return "";
	
	}
	//Mehtod to print all the attributes of a file in a single row.
	public void print_file(ArrayList<DirectoryFile> all_files){
		System.out.printf("%-30s %-20s %25s %30s\t %-40s %25s\n","File Name","Extension","Words","Lines","Last Modified","Size");
		System.out.printf("======================================================================================================================================================================================\n");
		for(int i=0;i<all_files.size();i++){
			if(all_files.get(i).get_file().isDirectory())
				continue;
			String date= date_format_change(all_files.get(i).get_last_modified());
			System.out.printf("%-30s %-20s %25d %30d\t %-40s %25d b\n",all_files.get(i).get_file_name(),all_files.get(i).get_type(),all_files.get(i).get_words(),all_files.get(i).get_lines(),date,all_files.get(i).get_size());
			//System.out.println("File Name: "+name+"\tExtension: "+type+"\tWords: "+words+"\tLines: "+lines+"\tLast Modified: "+date+"\tSize: "+size);
		}
	}
 	public String date_format_change(long epoch) {
        Date date = new Date(epoch);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
 	}
 	public void print_file_hierarchy(HashMap<PathMap,ArrayList<DirectoryFile>> map, PathMap root, int level){
 		for(int i=0;i<level;i++)
 			System.out.printf("\t");
 		String folder_name=root.path_string;
 		if(folder_name.contains("\\")) {
 			System.out.printf(folder_name.substring(folder_name.lastIndexOf('\\')+1)+"\n");
 		}else {
 			System.out.printf(folder_name+"\n");
 		}
 		ArrayList<DirectoryFile> al = map.get(root);
 		for(int i=0;i<al.size();i++) {
 			if(al.get(i).get_file().isDirectory()) {

 				print_file_hierarchy(map, new PathMap(root.path_string+"\\"+al.get(i).get_file_name()), level+1);
 			}
 			else {
 		 		for(int j=0;j<level+1;j++)
 		 			System.out.printf("\t");
 				System.out.println(al.get(i).get_file_name()+"."+al.get(i).get_type());
 			}
 		}
 	}
 	@Override
 	public boolean equals(Object o) {
 		if(o==this)
 			return true;
 		DirectoryFile f= (DirectoryFile)o;
 		if(f.get_file_name().equals(this.get_file_name()))
 			return true;
 		return false;
 	}
}
