package transferToSDB;
import java.io.*;
import java.util.*;

public class T2SDB {
	/* Input: window_size, path(csv file which is symbolic by SAX), class_table(the target attribute's class)
     * Output: Sequential Data Base
     *
     */
    public void translate_training(int window_size, String path, HashMap<Integer, String> class_table) {
       try {
           System.out.print("Transfer to SDB(Training): Reading \"" + path + "\"...");
           ArrayList<ArrayList<String>> records = readCSV(path);
           
           int training_data = (int)(records.size()*0.8);
           //The source cloumn
           int col = 2;
           
           //output
           File fout = new File("SDB(Training).txt");
	       FileOutputStream fos = new FileOutputStream(fout);
	       OutputStreamWriter osw = new OutputStreamWriter(fos);           
           
           for (int i = 1; i <= training_data; i++) { 
               //The calss_table's index
               int class_index = 0;             
               for (int j = 0; j < window_size;j++) {
                   int index = i + j;                     
                   if (index <= training_data) {
                       class_index = index;                     
                       osw.write(records.get(index).get(col) + " "+ -1 + " ");     
                   }                       
               }
               
               class_index = class_index + 1;
               if (class_index <= training_data) {
                   osw.write(class_table.get(class_index) + " "+ -1 + " ");
                   //Debug
                   //osw.write(class_table.get(class_index) + "(" + class_index + ")" + " "+ -1 + " ");
               }
               
               osw.write(""+-2);
               osw.write("\r\n");
               		
           }
           osw.close(); 
         
           System.out.println("done.");
           
       } catch (FileNotFoundException e) {
	    System.out.println("[ERROR] File Not Found Exception.");
	    e.printStackTrace();
	} catch (IOException e) {
           System.out.println("[ERROR] I/O Exception.");
           e.printStackTrace();
       }       
   } 
   
   public void translate_testing(int window_size, String path) {
       try {
           System.out.print("Transfer to SDB(Testing): Reading \"" + path + "\"...");
           ArrayList<ArrayList<String>> records = readCSV(path);
           
           //The source cloumn
           int col = 2;
           
           int training_data = (int)(records.size()*0.8);
           //Testing data size  
           int testing_data = 821;
           
           //output
           File fout = new File("SDB(Testing).txt");
	       FileOutputStream fos = new FileOutputStream(fout);
	       OutputStreamWriter osw = new OutputStreamWriter(fos);           
           
           for (int i = training_data + 1; i <= testing_data; i++) {                                   
               for (int j = 0; j < window_size;j++) {
                   int index = i + j;                     
                   if (index <= testing_data) {                                                   
                       osw.write(records.get(index).get(col) + " "+ -1 + " ");     
                   }                    
               }        
               osw.write(""+-2);
               osw.write("\r\n");
           }
           osw.close(); 
         
           System.out.println("done.");
           
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
   
}
