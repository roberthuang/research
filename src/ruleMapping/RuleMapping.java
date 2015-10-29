package ruleMapping;

import java.io.*;
import java.util.*;

public class RuleMapping {
	
    public HashMap<ArrayList<ArrayList<String>>, ArrayList<String>>  RuleMapping(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_testing) {
    	
       HashMap<ArrayList<ArrayList<String>>, ArrayList<String>> result = new HashMap<>();
       
        for (Integer i :  SDB_for_testing.keySet()) {
            //Mapping rule's number
            int number = 0;
            
            ArrayList<ArrayList<String>> itemsets = SDB_for_testing.get(i);
           
            HashMap<Integer, ArrayList<ArrayList<String>>> map = new HashMap<>();
            //For choose rule when mapping to the many rule
            HashMap<Integer, ArrayList<ArrayList<String>>> compare = new HashMap<>();
       
            for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
                //The size of mapping items in rule                
                int size = 0;
                int current = 0;
                for (int i_1 = 0; i_1 <  itemsets.size(); i_1++) {
                    for (int j = current; j < rule.size(); j++) {
                       
                        if (Collections.indexOfSubList(rule.get(j),itemsets.get(i_1)) != -1) {
                                
                            current = j;
                            current = current + 1;
                            size = size + 1;
                            break;
                        }
                    }
                
                }
                
                if (size == itemsets.size()) {
                    number = number + 1;       
                }
           
            } 
        }     
        
        return result;         	 	
    
    }
    
}
