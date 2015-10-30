package getAttribute;

import java.io.*;
import java.util.*;

public class GetAttr {
	/*  Input: length(period), records(csv file)
     *  Output: the class(Rise or Down) of target 
     *
     */
    public static HashMap<Integer, String> Move_Average(int length, ArrayList<ArrayList<String>> records) {
        System.out.printf("================Moving Average(%d)==================\n",length);
    	
    	
        HashMap<Integer, String> result = new HashMap<>(); 
        int training_data = (int)(records.size()*0.8);  
        //The column of Target
        int col = 1;                                                                                                                            
        for (int i = 1; i <= training_data; i++ ) {
        
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
        System.out.println("===================================================\n");  
      
        return result;
    }


}
