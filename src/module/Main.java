package module;
import java.io.*;
import java.util.*;

import dataPreprocessing.SAXTransformation;
import getAttribute.GetAttr;
import transferToSDB.T2SDB;

public class Main {
    public static void main(String[] args) {
	    //1.SAX(應該要先只針對Training data)
        SAXTransformation sax = new SAXTransformation();
        sax.start("SAXTransformation_config_petro_subset1.txt");
        
        try {
            String path = "petro_subset1.csv";//For Get Attribute
            
            //System.out.print("Reading \"" + path + "\"...\n");
            ArrayList<ArrayList<String>> records = readCSV(path);
        
        
        int window_size = 12;
        String path_of_file_after_SAX = "transformed_petro_subset1.csv";
        //2.Get Attribute        
        GetAttr g = new GetAttr();
        HashMap<Integer, String> class_table = g.Move_Average(3, records);    
        
        //3.Temporal Data Base to SDB(Training)
        T2SDB t = new T2SDB();
        
        t.translate_training(window_size, path_of_file_after_SAX,  class_table);
        //Temporal Data Base to SDB(Testing)
        //t.translate_testing(window_size, path_of_file_after_SAX);
        } catch (FileNotFoundException e) {
	        System.out.println("[ERROR] File Not Found Exception.");
	        e.printStackTrace();
	    }
                       
       
        
    }
    
    static ArrayList<ArrayList<String>> readCSV(String fullpath) throws FileNotFoundException{
        ArrayList<ArrayList<String>> records = new ArrayList<>();
	    File inputFile = new File(fullpath);
	    Scanner scl = new Scanner(inputFile);
	    while(scl.hasNextLine()){
		    ArrayList<String> newRecord = new ArrayList<>();
		    String[] tokens = scl.nextLine().split(",");
		    for(String token : tokens){
			    newRecord.add(token);
		    }
		    records.add(newRecord);
	    }
	    scl.close();
		
	    return records; 
    }
}


