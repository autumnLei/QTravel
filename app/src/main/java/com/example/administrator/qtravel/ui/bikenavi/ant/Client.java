package com.example.administrator.qtravel.ui.bikenavi.ant;

import java.util.ArrayList;

/** 
 * ??????????? 
 * @author lyq 
 * 
 */  
public class Client {  
    public static void main(String[] args){   
        //????????  
        int antNum;  
        //?????????????  
        int loopCount;  
        //???????  
        double alpha;  
        double beita;  
        double p;  
        double Q; 
        //????????
        String[][] value= {{"# CityName","1","2","3","4","5"},
        {"# Distance","1 2 1","1 3 1.4","1 4 5","1 5 1","2 3 1","2 4 5","2 5 1","3 4 5","3 5 1"}};
        ArrayList<String> result = new ArrayList<String>();
      
        antNum = 100;  
        alpha = 0.5;  
        beita = 1;  
        p = 0.5;  
        Q = 5;  
        loopCount = 100;  
        
        ACOTool tool = new ACOTool(value, antNum, alpha, beita, p, Q);  
        result = tool.antStartSearching(loopCount);
        for(String name: result){
        	System.out.print(name+" ");
        }
    }  
}  
