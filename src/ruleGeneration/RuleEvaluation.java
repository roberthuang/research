package ruleGeneration;

/****************************************************************************************************

 * 規則產生與評估 (RuleEvaluation)																		*

 * 輸入：欲處理的txt格式情節樣式檔案、json設定檔。															*

 * 處理：根據輸入之情節樣式集產生規則集，並根據設定之最小信心度進行規則篩選；若有設定規則過濾條件 (contains_event)	*

 * 　　　　時將避免產生不含指定事件的規則。																	*

 * 輸出：於指定路徑下輸出產生的規則集檔案。																*

 * 設定檔格式：																						*

 *��- path：輸入之情節樣式集檔案的檔案路徑。																*

 *��- output：輸出產生之規則集檔案的檔案路徑。															*

 *��- min_conf：最小信心度，值域為(0, 1]的浮點數。														*

 *��- contains_event：一字串陣列，指定產生之規則必須包含的事件；空陣列代表不進行過濾。							*

 ****************************************************************************************************/



import java.io.File;

import java.io.FileNotFoundException;

import java.io.FileReader;

import java.io.FileWriter;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Collections;

import java.util.Comparator;

import java.util.HashMap;

import java.util.HashSet;

import java.util.Iterator;

import java.util.Scanner;



import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;



public class RuleEvaluation {

	static double min_conf;

	

	static class RuleEval{

		double sup = 0;

		double conf = 0;

		

		public RuleEval(double sup, double conf){

			this.sup = sup;

			this.conf = conf;

		}

	}


	public static void start(String jsconfig, double min_conf_input) {

		mainflow(jsconfig, min_conf_input);

	}

	

	static void mainflow(String jsconfig, double min_conf_input) {

		System.out.println("===============  RuleEvaluation ===================");

		JSONParser parser = new JSONParser();

		String path = "", output_filename = "";

		HashSet<String> contains_event = new HashSet<>();

		try {

			JSONObject jsobj = (JSONObject) parser.parse(new FileReader(jsconfig));

			path = jsobj.get("path").toString();

			output_filename = jsobj.get("output").toString();

			//min_conf = Double.parseDouble(jsobj.get("min_conf").toString());
			min_conf = min_conf_input;

			JSONArray js_events = (JSONArray) jsobj.get("contains_event");

			for(int i=0;i<js_events.size();i++){

				contains_event.add(js_events.get(i).toString());

			}

		} catch (Exception e) {

			System.out.println("[ERROR] Failed at JSON parsing.");

			e.printStackTrace();

			System.exit(0);

		}

		

		try {

			//1. read patterns

			HashMap<String, Double> patterns = readPatterns(path);

			//2. generate rules

			HashMap<String, RuleEval> rules = generateRules(patterns, contains_event);

			//3. output to file

			writeFile(output_filename, rules);

			System.out.println("Total: " + rules.size() + " rules are generated.");

		} catch (FileNotFoundException e) {

			System.out.println("[ERROR] Path setting error.");

			e.printStackTrace();

		}

		System.out.println("===================================================\n");

	}

	

	static HashMap<String, Double> readPatterns(String filename) throws FileNotFoundException{

	        //traing_data_size
	        double traing_data = 656;

	        

		HashMap<String, Double> patterns = new HashMap<>();

		Scanner sc = new Scanner(new File(filename));

		while(sc.hasNextLine()){

			String[] tokens = sc.nextLine().split(" -1  #SUP: ");

			double sup = Double.parseDouble(tokens[1])/traing_data;
            
				

			patterns.put(tokens[0], sup);

		}

		sc.close();

		return patterns;

	}

	

	static HashMap<String, RuleEval> generateRules(HashMap<String, Double> patterns, HashSet<String> contains_event){

	        

		HashMap<String, RuleEval> rules = new HashMap<>();

		Iterator<String> keys = patterns.keySet().iterator();

		String default_predict = null;

		while(keys.hasNext()){

			String key = keys.next();

			double sup = patterns.get(key);

			String[] items = key.split(" -1 ");

			boolean keep = false;

			

			for(int i=0;i<items.length && !keep;i++){

				if(contains_event.size() == 0 || contains_event.contains(items[i])) keep = true;

			}

			if(!keep) continue;

			int splitPoint = 0;

			for(int i=1;i<items.length;i++){

				if(items[i].charAt(0) != '_') splitPoint = i;

			}

			if(splitPoint == 0){

				if(contains_event.contains(key)

						&& (default_predict == null || patterns.get(default_predict) < patterns.get(key)))

					default_predict = items[0];

				continue;	//Only one event -> cannot generate rules

			}

			StringBuilder LHS = new StringBuilder(items[0]),

						  RHS = new StringBuilder(items[splitPoint]);

			for(int i=1;i<splitPoint;i++) LHS.append(" -1 ").append(items[i]);

			

			for(int i=splitPoint+1;i<items.length;i++) RHS.append(" -1 ").append(items[i]);

			String rule = new StringBuilder(LHS).append(" -> ").append(RHS).toString();

			if(!patterns.containsKey(LHS.toString())) System.out.println(LHS + "\t->\t" + RHS);

			double conf = 1.0 * sup / patterns.get(LHS.toString());
            
			
			if(conf >= min_conf) rules.put(rule, new RuleEval(sup, conf));

		}

		if(default_predict != null){

			String rule = new StringBuilder("DEFAULT -> ").append(default_predict).toString();

			double sup = patterns.get(default_predict);

			rules.put(rule, new RuleEval(sup, sup));	//Confidence of DEFAULT = its support

		}

		return rules;

	}

	

	static void writeFile(String filename, final HashMap<String, RuleEval> rules){

		try {

			FileWriter fw = new FileWriter(new File(filename));

			ArrayList<String> keys = new ArrayList<>(rules.keySet());

			Collections.sort(keys, new Comparator<String>() {	//sort rules by confidence / support / length of LHS; DEFAULT rule is the last

				@Override

				public int compare(String key1, String key2) {

					if(key1.split(" -> ")[0].compareTo("DEFAULT") == 0) return 1;

					else if(key2.split(" -> ")[0].compareTo("DEFAULT") == 0) return -1;

					RuleEval rule1 = rules.get(key1), rule2 = rules.get(key2);

					if(rule1.conf > rule2.conf) return -1;

					else if(rule1.conf == rule2.conf){

						if(rule1.sup > rule2.sup) return -1;

						else if(rule1.sup == rule2.sup){

							int length1 = key1.split(" -1 ").length,

								length2 = key2.split(" -1 ").length;

							return length1 - length2;

						}else return 1;

					}else return 1;

				}

			});

			

			for(int i=0;i<keys.size();i++){

				String key = keys.get(i);

				fw.write(key + "\t:\t" + rules.get(key).sup + ",\t" + rules.get(key).conf + "\r\n");

			}

			fw.close();

		} catch (IOException e) {

			System.out.println("[ERROR] I/O Exception.");

			e.printStackTrace();

		}

		

	}

}