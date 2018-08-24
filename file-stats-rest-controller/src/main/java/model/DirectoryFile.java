package model;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


//This class stores all the attributes of a single file along with all the member functions that are needed to calculate the values of the attributes.

public class DirectoryFile implements Serializable{

	private File file;
	private String type;
	private String name;
	private int lines;
	private long words;
	private long size;
	private long last_modified; 
	private boolean flag=true;
	private HashMap<String,Integer> tokens= new HashMap<String,Integer>();
	HashSet<String> stopwords= new HashSet<>(Arrays.asList("a","an","the","of","on"));

	//A non-parameterised constructor used to instantiate an object that is used to invoke functions in other classes
	DirectoryFile(){

	}
	//A parameterised Constructor that sets all the attributes of a file by calling suitable functions.
	DirectoryFile(File f){
		file = f;
		CalculateStats th= new CalculateStats();
		th.setFile(this);
		th.start();
	}
/*
		if(!file.isDirectory()) {
			try{
				type = set_type(file);
				name = set_name(file);
				set_lines();
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
*/

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

	public HashMap<String,Integer> getTokens(){
		return tokens;
	}

	//Method to set last modified timestamp
	public void setLastModified(long lastModified){

		this.last_modified=lastModified;

	}
	//Method to set the size of the file
	public void setSize(long size){

		this.size=size;

	}


	//Method to set the number of lines in the file
	public void setLines(int lines){
		this.lines= lines;
	}
	

	////Method to set the Name of the file
	public void setName(String name){
		this.name= name;
	}
	

	////Method to set the file type
	public void setType(String type){
		this.type= type;
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




	//Method to set the number of words in the file
	public void setWords(long words){
		this.words=words;
	}

	public void setTokens(HashMap<String,Integer> tokens){
		this.tokens=tokens;
	}



}
