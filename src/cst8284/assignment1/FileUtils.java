package cst8284.assignment1;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javafx.stage.Stage;

public class FileUtils {

	private static String absPath = "/Users/nikhil/Desktop";
	
	public static File getToDoFile(Stage ps){
		
		File toDoFile = new File("/Users/nikhil/Desktop/ToDoList.todo");
		setAbsPath(toDoFile);
		return toDoFile;
	}
	
	public ArrayList<ToDo> getToDoArray(String fileName) {
		ArrayList<ToDo> toDos = new ArrayList<>();
		try {
			FileInputStream fis = getFIStreamFromAbsPath(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while(true) toDos.add((ToDo)(ois.readObject()));
		} catch (EOFException e){
			ArrayList<ToDo> al = toDos;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return toDos;
	}
	
	public static FileInputStream getFIStreamFromAbsPath(String absPath){
		FileInputStream fis = null;
		try {
     		fis = new FileInputStream(absPath);
		} catch (IOException e){
			e.printStackTrace();
		}
		return fis;
	}
	
	public static String getAbsPath() {
		return absPath;
	}

	public static String getAbsPath(File f) {
		return f.getAbsolutePath();
	}
	
	private static void setAbsPath(String path){
		absPath = path;
	}
	
	private static void setAbsPath(File f) { 
		absPath = (fileExists(f))? f.getAbsolutePath():""; 
	}
	
	private static Boolean fileExists(File f) {
		return (f != null && f.exists() && f.isFile() && f.canRead());
	}
	
	private static String getDefaultFolder(){
		String s = getAbsPath();
		return(s.substring(0, s.lastIndexOf("\\")+1));
	}
	
}
