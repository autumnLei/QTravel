package com.example.administrator.qtravel.ui.bikenavi.ant;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileReader;  
import java.io.IOException;  
import java.text.MessageFormat;  
import java.util.ArrayList;  
import java.util.Collections;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.Random;  
  
/** 
 * ??????????? 
 *  
 * @author lyq 
 *  
 */  
public class ACOTool {  
    // ????????????  
    public static final int INPUT_CITY_NAME = 1;  
    public static final int INPUT_CITY_DIS = 2;  
  
    // ???м??????????  
    public static double[][] disMatrix;  
    // ??????  
    public static int currentTime;  
  
    // ??????????  
    private String[][] value;  
    // ????????  
    private int antNum;  
    // ???????  
    private double alpha;  
    private double beita;  
    private double p;  
    private double Q;  
    // ???????????  
    private Random random;  
    // ???????????,????????????????????????  
    private ArrayList<String> totalCitys;  
    // ???е????????  
    private ArrayList<Ant> totalAnts;  
    // ???м??????????????????????????????  
    private double[][] pheromoneMatrix;  
    // ???????·??,????????????????????  
    private ArrayList<String> bestPath;  
    // ????????洢?,key???????(i,j,t)->value  
    private Map<String, Double> pheromoneTimeMap;  
  
    public ACOTool(String[][] value, int antNum, double alpha, double beita,  
            double p, double Q) {  
        this.value=value;  
        this.antNum = antNum;  
        this.alpha = alpha;  
        this.beita = beita;  
        this.p = p;  
        this.Q = Q;  
        this.currentTime = 0;  
  
        readDataFile();  
    }  
  
    /** 
     * ??????ж?????? 
     */  
    private void readDataFile() {   
        ArrayList<String[]> dataArray = new ArrayList<String[]>();  
        for(int i = 0; i < value.length; i++) {
        	for(int j = 0; j < value[i].length; j++) {
        		dataArray.add(value[i][j].split(" "));
        	}
        }
  
        int flag = -1;  
        int src = 0;  
        int des = 0;  
        int size = 0;  
        // ???г????????????????  
        this.totalCitys = new ArrayList<>();  
        for (String[] array : dataArray) {  
            if (array[0].equals("#") && totalCitys.size() == 0) {  
                flag = INPUT_CITY_NAME;  
  
                continue;  
            } else if (array[0].equals("#") && totalCitys.size() > 0) {  
                size = totalCitys.size();  
                // ????????????  
                this.disMatrix = new double[size + 1][size + 1];  
                this.pheromoneMatrix = new double[size + 1][size + 1];  
  
                // ????-1???????λ?????  
                for (int i = 0; i < size; i++) {  
                    for (int j = 0; j < size; j++) {  
                        this.disMatrix[i][j] = -1;  
                        this.pheromoneMatrix[i][j] = -1;  
                    }  
                }  
  
                flag = INPUT_CITY_DIS;  
                continue;  
            }  
  
            if (flag == INPUT_CITY_NAME) {  
                this.totalCitys.add(array[0]);  
            } else {  
                src = Integer.parseInt(array[0]);  
                des = Integer.parseInt(array[1]);  
  
                this.disMatrix[src][des] = Double.parseDouble(array[2]);  
                this.disMatrix[des][src] = Double.parseDouble(array[2]);  
            }  
        }  
    }  
  
    /** 
     * ????????????i??j????? 
     *  
     * @param cityI 
     *            ????I 
     * @param cityJ 
     *            ????J 
     * @param currentTime 
     *            ?????? 
     * @return 
     */  
    private double calIToJProbably(String cityI, String cityJ, int currentTime) {  
        double pro = 0;  
        double n = 0;  
        double pheromone;  
        int i;  
        int j;  
  
        i = Integer.parseInt(cityI);  
        j = Integer.parseInt(cityJ);  
  
        pheromone = getPheromone(currentTime, cityI, cityJ);  
        n = 1.0 / disMatrix[i][j];  
  
        if (pheromone == 0) {  
            pheromone = 1;  
        }  
  
        pro = Math.pow(n, alpha) * Math.pow(pheromone, beita);  
  
        return pro;  
    }  
  
    /** 
     * ???????????????I???????J???е???? 
     *  
     * @return 
     */  
    public String selectAntNextCity(Ant ant, int currentTime) {  
        double randomNum;  
        double tempPro;  
        // ????????  
        double proTotal;  
        String nextCity = null;  
        ArrayList<String> allowedCitys;  
        // ?????и????  
        double[] proArray;  
  
        // ?????????????????·???κγ??У?????????????????  
        if (ant.currentPath.size() == 0) {  
            nextCity = String.valueOf(random.nextInt(totalCitys.size()) + 1);  
  
            return nextCity;  
        } else if (ant.nonVisitedCitys.isEmpty()) {  
            // ??????????????????λ?????  
            nextCity = ant.currentPath.get(0);  
  
            return nextCity;  
        }  
  
        proTotal = 0;  
        allowedCitys = ant.nonVisitedCitys;  
        proArray = new double[allowedCitys.size()];  
  
        for (int i = 0; i < allowedCitys.size(); i++) {  
            nextCity = allowedCitys.get(i);  
            proArray[i] = calIToJProbably(ant.currentPos, nextCity, currentTime);  
            proTotal += proArray[i];  
        }  
  
        for (int i = 0; i < allowedCitys.size(); i++) {  
            // ?????????  
            proArray[i] /= proTotal;  
        }  
  
        // ???????????????????  
        randomNum = random.nextInt(100) + 1;  
        randomNum = randomNum / 100;  
        // ???1.0??????ж?????,??????????1.0??0.99???ж?  
        if (randomNum == 1) {  
            randomNum = randomNum - 0.01;  
        }  
  
        tempPro = 0;  
        // ???????  
        for (int j = 0; j < allowedCitys.size(); j++) {  
            if (randomNum > tempPro && randomNum <= tempPro + proArray[j]) {  
                // ??????????????????????  
                nextCity = allowedCitys.get(j);  
                break;  
            } else {  
                tempPro += proArray[j];  
            }  
        }  
  
        return nextCity;  
    }  
  
    /** 
     * ?????????????????i??????j?????????? 
     *  
     * @param t 
     * @param cityI 
     * @param cityJ 
     * @return 
     */  
    private double getPheromone(int t, String cityI, String cityJ) {  
        double pheromone = 0;  
        String key;  
  
        // ????????轫??????????  
        key = MessageFormat.format("{0},{1},{2}", cityI, cityJ, t);  
  
        if (pheromoneTimeMap.containsKey(key)) {  
            pheromone = pheromoneTimeMap.get(key);  
        }  
  
        return pheromone;  
    }  
  
    /** 
     * ?????????????????????? 
     *  
     * @param t 
     */  
    private void refreshPheromone(int t) {  
        double pheromone = 0;  
        // ????????????????????????????????????в???  
        double lastTimeP = 0;  
        // ??????????????????  
        double addPheromone;  
        String key;  
  
        for (String i : totalCitys) {  
            for (String j : totalCitys) {  
                if (!i.equals(j)) {  
                    // ????????轫??????????  
                    key = MessageFormat.format("{0},{1},{2}", i, j, t - 1);  
  
                    if (pheromoneTimeMap.containsKey(key)) {  
                        lastTimeP = pheromoneTimeMap.get(key);  
                    } else {  
                        lastTimeP = 0;  
                    }  
  
                    addPheromone = 0;  
                    for (Ant ant : totalAnts) {  
                        if(ant.pathContained(i, j)){  
                            // ???????????????????????????????????  
                            addPheromone += Q / ant.calSumDistance();  
                        }  
                    }  
  
                    // ????ε??????????????????????????  
                    pheromone = p * lastTimeP + addPheromone;  
                    key = MessageFormat.format("{0},{1},{2}", i, j, t);  
                    pheromoneTimeMap.put(key, pheromone);  
                }  
            }  
        }  
  
    }  
  
    /** 
     * ?????????????  ??????????·??
     * @param loopCount 
     * ??????????? 
     */  
    public ArrayList<String> antStartSearching(int loopCount) {  
        // ????????????  
        int count = 0;  
        // ??е??????????  
        String selectedCity = "";  
  
        pheromoneTimeMap = new HashMap<String, Double>();  
        totalAnts = new ArrayList<>();  
        random = new Random();  
  
        while (count < loopCount) {  
            initAnts();  
  
            while (true) {  
                for (Ant ant : totalAnts) {  
                    selectedCity = selectAntNextCity(ant, currentTime);  
                    ant.goToNextCity(selectedCity);  
                }  
  
                // ???????????????г??У??????????????  
                if (totalAnts.get(0).isBack()) {  
                    break;  
                }  
            }  
  
            // ??????????  
            currentTime++;  
            refreshPheromone(currentTime);  
            count++;  
        }  
  
        // ?????????????????????????????·??  
        Collections.sort(totalAnts);  
        bestPath = totalAnts.get(0).currentPath;  
//        System.out.println(MessageFormat.format("????{0}???????????????ó??????·????", count));  
//        System.out.print("entrance");  
//        for (String cityName : bestPath) {  
//            System.out.print(MessageFormat.format("-->{0}", cityName));  
//        }  
        return bestPath;
    }  
  
    /** 
     * ???????????? 
     */  
    private void initAnts() {  
        Ant tempAnt;  
        ArrayList<String> nonVisitedCitys;  
        totalAnts.clear();  
  
        // ????????  
        for (int i = 0; i < antNum; i++) {  
            nonVisitedCitys = (ArrayList<String>) totalCitys.clone();  
            tempAnt = new Ant(pheromoneMatrix, nonVisitedCitys);  
            totalAnts.add(tempAnt);  
        }  
    }  
}  