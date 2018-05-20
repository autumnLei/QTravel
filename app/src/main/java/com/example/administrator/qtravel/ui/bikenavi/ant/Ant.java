package com.example.administrator.qtravel.ui.bikenavi.ant;

import java.util.ArrayList;


public class Ant implements Comparable<Ant>{
	
	String currentPos;  
    // ????????????????????????  
    Double sumDistance;  
    // ???技??????????????????????????????  
    double[][] pheromoneMatrix;  
    // ??????????????技???  
    ArrayList<String> visitedCitys;  
    // ??汛???????技???  
    ArrayList<String> nonVisitedCitys;
    // ???????????﹞??  
    ArrayList<String> currentPath;  
  
    public Ant(double[][] pheromoneMatrix, ArrayList<String> nonVisitedCitys) {  
        this.pheromoneMatrix = pheromoneMatrix;  
        this.nonVisitedCitys = nonVisitedCitys;  
  
        this.visitedCitys = new ArrayList<>();  
        this.currentPath = new ArrayList<>();  
    }  
  
    /** 
     * ????﹞????????(????) 
     *  
     * @return 
     */  
    public double calSumDistance() {  
        sumDistance = 0.0;  
        String lastCity;  
        String currentCity;  
  
        for (int i = 0; i < currentPath.size() - 1; i++) {  
            lastCity = currentPath.get(i);  
            currentCity = currentPath.get(i + 1);  
  
            // ????????????技???  
            sumDistance += ACOTool.disMatrix[Integer.parseInt(lastCity)][Integer  
                    .parseInt(currentCity)];  
        }  
  
        return sumDistance;  
    }  
  
    /** 
     * ??????????????????? 
     *  
     * @param city 
     *            ???????? 
     */  
    public void goToNextCity(String city) {  
        this.currentPath.add(city);  
        this.currentPos = city;  
        this.nonVisitedCitys.remove(city);  
        this.visitedCitys.add(city);  
    }  
  
    /** 
     * ?忪?????????????????????? 
     *  
     * @return 
     */  
    public boolean isBack() {  
        boolean isBack = false;  
        String startPos;  
        String endPos;  
  
        if (currentPath.size() == 0) {  
            return isBack;  
        }  
  
        startPos = currentPath.get(0);  
        endPos = currentPath.get(currentPath.size() - 1);  
        if (currentPath.size() > 1 && startPos.equals(endPos)) {  
            isBack = true;  
        }  
  
        return isBack;  
    }  
  
    /** 
     * ?忪?????????汍??????﹞???????????????i??????j 
     *  
     * @param cityI 
     *            ????I 
     * @param cityJ 
     *            ????J 
     * @return 
     */  
    public boolean pathContained(String cityI, String cityJ) {  
        String lastCity;  
        String currentCity;  
        boolean isContained = false;  
  
        for (int i = 0; i < currentPath.size() - 1; i++) {  
            lastCity = currentPath.get(i);  
            currentCity = currentPath.get(i + 1);  
  
            // ???????﹞??????竹????????????抉????????  
            if ((lastCity.equals(cityI) && currentCity.equals(cityJ))  
                    || (lastCity.equals(cityJ) && currentCity.equals(cityI))) {  
                isContained = true;  
                break;  
            }  
        }  
  
        return isContained;  
    }  
	
	@Override
	public int compareTo(Ant o) {
		// TODO Auto-generated method stub
		return this.sumDistance.compareTo(o.sumDistance);  
	}

}
