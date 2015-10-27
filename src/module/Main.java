package module;

import dataPreprocessing.SAXTransformation;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");	
        SAXTransformation sax = new SAXTransformation();
       
        sax.start(args[0]);
    }
}
