package getAttribute;

import java.io.*;
import java.util.*;

public class GetAttr {
	private static HashMap<Integer, Double> temp_sl = new HashMap<>();
	private static HashMap<Integer, Double> temp_ll = new HashMap<>();
	/*  Input: length(period), records(csv file)
     *  Output: the class(Rise or Down) of target attribute
     *
     */
    public static HashMap<Integer, String> Move_Average(int length, ArrayList<ArrayList<String>> records) {
        System.out.printf("================Moving Average(%d)==================\n",length); 	
        HashMap<Integer, String> result = new HashMap<>(); 
        int training_data = (int)((records.size()-1)*0.8);  
        System.out.println("Training Data Size: " + training_data);
        System.out.println("Record Data Size: " + records.size());
        
        //The column of Target
        int col = 1;                                                                                                                            
        for (int i = 1; i < records.size(); i++ ) {
        
            if (i <= length) {
                if (i == length) {
                    result.put(i, "Rise");     
                }
                continue;
            }
            
            double sum_t = 0;
            double sum_t_1 = 0;
            if (i - length + 1 >= 1) {
             
                for (int p_1 = i; p_1 >= i-length+1; p_1--) {                
                    sum_t = sum_t + Double.parseDouble(records.get(p_1).get(col));
                } 
                     
                int j = i - 1;
                if (j - length + 1 >=1) {
                    
                    for (int p_2 = j; p_2 >= j-length+1; p_2--) {
                       
                        sum_t_1 = sum_t_1 + Double.parseDouble(records.get(p_2).get(col));
                    }
                }
                
            }  
            
            //Rise or Down
            double MA = sum_t/length - sum_t_1/length;     
            if (MA >= 0) {
                //System.out.println("i: " + i + " " + MA);
                result.put(i, "Rise");    
            } else {
                //System.out.println("i: " + i + " " + MA);
                result.put(i, "Down"); 
            }              
        }  
        /*
        //Debug 
        for (Integer i :result.keySet()) {
        	System.out.println(i + " " + result.get(i));
        	
        	
        }*/
        System.out.println("Moving avearge number :" + result.size());
        System.out.println("===================================================\n");  
      
        return result;
    }
    
    public static HashMap<Integer, String> MACD(int sl, int ll, int tl, ArrayList<ArrayList<String>> records) {
    	System.out.printf("================MACD(sl=%d,ll=%d,tl=%d)==================\n", sl, ll, tl);
    	HashMap<Integer, String> result = new HashMap<>(); 
    	for (int i = 1; i < records.size(); i++) {
    	    double MACD = DIF(i, sl, ll, records) - DEM(i, sl, ll, tl, records);        	
    		if (MACD <= 0) {
    			result.put(i, "Down");
    		} else {
    			result.put(i, "Rise");			
    		}
    	}
    	System.out.println("Moving avearge number :" + result.size());
        System.out.println("===================================================\n"); 
    	return result;
    } 
     
    public static double EMA(int t, int l, ArrayList<ArrayList<String>> records, String s) {
    	if (t == 0) {  
    		return 0.0;
    	}
    	int col = 1;
    	double alpha = 2/(double)(l+1);
    	double p = Double.parseDouble(records.get(t).get(col));
        if (s.equals("sl")) {
        	temp_sl.put(0, 0.0);
            temp_sl.put(t, temp_sl.get(t-1) + alpha*(p - temp_sl.get(t-1)));
            return temp_sl.get(t);
        } else {  	
        	temp_ll.put(0, 0.0); 
            temp_ll.put(t, temp_ll.get(t-1) + alpha*(p - temp_ll.get(t-1)));
            return temp_ll.get(t);
        }  
    	
    }
    
    public static double DIF(int t, int sl, int ll, ArrayList<ArrayList<String>> records) {
        return EMA(t, sl, records, "sl") - EMA(t, ll, records, "ll"); 	
    }
    
    public static double DEM(int t, int sl, int ll, int tl, ArrayList<ArrayList<String>> records) {
        return 	(DIF(t, sl, ll, records) + DIF(t-1, sl, ll, records))/(double) tl;
    }

}
