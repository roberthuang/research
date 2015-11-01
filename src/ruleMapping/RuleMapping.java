package ruleMapping;

import java.io.*;
import java.util.*;

public class RuleMapping {
	
    public HashMap<Integer, ArrayList<String>>  RuleMapping(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules,
    HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_testing) {
        //1.We evaluate the number of Rise and Down in rules.
        HashMap<String, Integer> number_of_rise_down = new HashMap<>();
        for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
        	String rise_down = rule.get(rule.size()-1).get(0);
            if (number_of_rise_down.get(rise_down) == null) {
            	number_of_rise_down.put(rise_down, 1);    	           	
            } else {
                int number = number_of_rise_down.get(rise_down);
                number = number + 1;
                number_of_rise_down.put(rise_down , number);
            } 	   
        }
        
        ArrayList<String> answer = new ArrayList<>();
        if (number_of_rise_down.get("Rise") == null) {
        	answer.add("Down");
        	
        } else if (number_of_rise_down.get("Down") == null) {
        	answer.add("Rise"); 
        } else {
        	int Rise_number =  number_of_rise_down.get("Rise");
            int Down_number =  number_of_rise_down.get("Down");
            if (Rise_number > Down_number) {
                answer.add("Rise");        	
            } else {
            	answer.add("Down");   
            }	
        }
        
    	 	
    	//2.Begin Mapping 
        HashMap<Integer, ArrayList<String>> result = new HashMap<>();

       
        for (Integer i : SDB_for_testing.keySet()) {
            //Match rule's number
            int match_number = 0;
            ArrayList<ArrayList<ArrayList<String>>> match_rules = new  ArrayList<>();
            
            //The sequence in SDB_Testing
            ArrayList<ArrayList<String>> itemsets = SDB_for_testing.get(i);
            
            
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
                	match_number = match_number + 1;      
                	match_rules.add(rule);
                }
               
            } 
            //System.out.println(match_number);
            
            if (match_number >=2) {
                int max = 0;
                double max_sup = rules.get(match_rules.get(0)).get(0);
                double max_confidence = rules.get(match_rules.get(0)).get(1);              
                for (int j = 1; j < match_rules.size(); j++) {
                	double confidence = rules.get(match_rules.get(j)).get(1);
                    if (confidence > max_confidence) {
                        max = j;	
                    } else if (confidence == max_confidence) {
                    	double sup = rules.get(match_rules.get(j)).get(0);
                    	if (sup > max_sup) {
                    		max = j;          		    
                    	}
                    }
                	
                }
            	
                ArrayList<ArrayList<String>> match_rule = match_rules.get(max);
                ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
            	result.put(i, Rise_Down);
            	
            } else if (0< match_number && match_number < 2){
            	//Only one match_rule
            	ArrayList<ArrayList<String>> match_rule = match_rules.get(0);
            	ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
            	result.put(i,Rise_Down);
            	
            } else {
            	result.put(i,answer);
            	
            }
            //Debug
            //System.out.println(i);
        }    
        /*
        //Debug
        for (ArrayList<ArrayList<String>> key : result.keySet()) {
            System.out.println(key + " " + result.get(key));
        		
        }*/
        //System.out.println(result.size());
        return result;         	 	
    
    }
    
    public double evaluate(HashMap<Integer, String> class_table , HashMap<Integer, ArrayList<String>> predict) {
    	//Argument
        int True_Positive  = 0;
        int True_Negative  = 0;
        int False_Positive = 0;
        int False_Negative = 0;
        int testing_data = 165;
        int index = 656;
        
        for (int i = 1; i <= testing_data; i++) {
            String rise_down = predict.get(i).get(0);
            //True Positive
            if (rise_down == "Rise") {
            	 if (class_table.get(index+i) == "Rise") {
            		 True_Positive =  True_Positive + 1;	 	 
            	 } else {
            		 False_Positive = False_Positive + 1; 
            	 }
            	 
            } else  if (rise_down == "Down") {
            	 if (class_table.get(index+i) == "Down") {
            		 True_Negative =  True_Negative + 1;	 	 
            	 } else {
            		 False_Negative = False_Negative + 1; 
            	 }     	
            }	
        }
        double result = (True_Positive + True_Negative)/ (double)(True_Positive + True_Negative + False_Positive + False_Negative);
      
   
    	//System.out.println("Precision: " + result);
        return result;
    	
    }
    
}
