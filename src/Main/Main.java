package Main;
import java.io.*;
import java.util.*;
import java.net.URL;

import dataPreprocessing.SAXTransformation;
import getAttribute.GetAttr;
import ruleGeneration.RuleEvaluation;
import ruleMapping.RuleMapping;
import transferToSDB.T2SDB;

import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.AlgoPrefixSpan_with_Strings;
import ca.pfv.spmf.input.sequence_database_list_strings.SequenceDatabase;

public class Main {
    public static void main(String[] args) {
    	
	    /**1.SAX(���ӭn���u�w��Training data,�ݭn��)**/
    	System.out.println("##Step1: SAX");
        SAXTransformation sax = new SAXTransformation();
        sax.start("SAXTransformation_config_petro_subset1.txt");
        
        try {
        	
            String path = "petro_subset1.csv";//For Get Attribute
            
            //System.out.print("Reading \"" + path + "\"...\n");
            ArrayList<ArrayList<String>> records = readCSV(path);
      
            int window_size = 12;
            String path_of_file_after_SAX = "transformed_petro_subset1.csv";
            /**2.Get Attribute**/ 
            System.out.println("##Step2: GetAttribute");
            GetAttr g = new GetAttr();
            HashMap<Integer, String> class_table = g.Move_Average(3, records);    
        
            /**3.Temporal Data Base to SDB(Training)**/
            System.out.println("##Step3: Temporal Data Base to SDB(Training)");
            T2SDB t = new T2SDB();
            t.translate_training(window_size, path_of_file_after_SAX,  class_table);
            
            //Temporal Data Base to SDB(Testing)
            //t.translate_testing(window_size, path_of_file_after_SAX);
            
            
            /**4.Sequential Pattern Mining**/
            System.out.println("##Step4: Sequential Pattern Mining(Training)");
            // Load a sequence database
            SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
            sequenceDatabase.loadFile("C:\\user\\workspace\\research\\SDB(Training).txt");
            //print the database to console
            //sequenceDatabase.print();
    		
    		// Create an instance of the algorithm with minsup = 50 %
    		AlgoPrefixSpan_with_Strings algo = new AlgoPrefixSpan_with_Strings(); 
    		int minsup = 100; 
    		// execute the algorithm
    		algo.runAlgorithm(sequenceDatabase, "C:\\user\\workspace\\research\\sequential_patterns.txt", minsup);    
    		algo.printStatistics(sequenceDatabase.size());
    		
    		
    		/**5.Rule Generation**/
    		
    		System.out.println("##Step5: Rule Generation");
    		RuleEvaluation rule = new RuleEvaluation();
    		rule.start("RuleEvaluation_config.txt");
    		
    		
    		/**6.Rule Mapping**/
    		System.out.println("##Step6: Rule Mapping");
    		RuleMapping mapping = new RuleMapping();
    		mapping.RuleMapping(readRules("rules.txt"), ReadSDB_for_testing("SDB(Testing).txt"));
    		 
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File Not Found Exception.");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("[ERROR] I/O Exception.");
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
    
    static HashMap<Integer, ArrayList<ArrayList<String>>> ReadSDB_for_testing(String filename) throws FileNotFoundException{
        HashMap<Integer, ArrayList<ArrayList<String>>> result = new HashMap<>();
        int index = 1;
        
        Scanner sc = new Scanner(new File(filename));
        
        while(sc.hasNextLine()) {
        
            ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
         
            String[] tokens = sc.nextLine().split(" -1 -2");
            String[] tokens_next = tokens[0].split(" -1 ");
            for (String s : tokens_next) {
                ArrayList<String> itemset = new ArrayList<>();
                String[] tokens_next_next = s.split(" ");
                for (String ss : tokens_next_next) {
                    itemset.add(ss);
                }
                itemsets.add(itemset);
            }
            result.put(index, itemsets);
            index = index + 1;
        }     
        /*
        //debug
        for (Integer i : result.keySet()) {
	        System.out.println(i + " " + result.get(i));
	    
	    }*/
	
        sc.close();
        return result;
        
    }
     
    //Read rule file
    static HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> readRules(String filename) throws FileNotFoundException{
	        
		HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> result = new HashMap<>();
				
		Scanner sc = new Scanner(new File(filename));
		while(sc.hasNextLine()){
		
		        ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
		        ArrayList<Double> list = new ArrayList<>();
			String[] tokens = sc.nextLine().split("\t:\t");
			//For sup, confidence
			String[] number = tokens[1].split(",\t");
			for (String s : number) {
			    double n = Double.parseDouble(s);
			    list.add(n);
			}
			//For items
			String[] tokens_next = tokens[0].split(" -> ");
			String[] tokens_next_next = tokens_next[0].split(" -1 ");
			
			//tokens_next[1] : Rise/Down
			ArrayList<String> itemset_next = new ArrayList<>();
			itemset_next.add(tokens_next[1]);
			
			for(String s : tokens_next_next) {
			    String[] tokens_next_next_next =  s.split(" ");
			    ArrayList<String> itemset = new ArrayList<>();   
			    for(String ss : tokens_next_next_next) {
			        itemset.add(ss);    			    
			    }
			    itemsets.add(itemset);
                        }
			itemsets.add(itemset_next);		
			result.put(itemsets, list);
		}
		/*
		//debug
		for (ArrayList<ArrayList<String>> key : result.keySet()) {
		    System.out.println(key + " " + result.get(key));
		
		}
		*/
		sc.close();
		return result;	
    }
  
}

