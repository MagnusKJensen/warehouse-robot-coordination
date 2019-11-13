package dk.aau.d507e19.warehousesim.statistics;

import java.util.ArrayList;

public class AllSeedsOverview {
    private int seedsVisited = 0;
    /**
     * General stats
     */
    private ArrayList<Double> availableProductsLeftAverages = new ArrayList<>();
    private ArrayList<Double> ordersInQueueAverages = new ArrayList<>();
    private ArrayList<Double> ordersPerMinuteAverages = new ArrayList<>();
    private double ultimateLowestOrderPerMinute = -1;
    private double ultimateHighestOrdersPerMinute = -1;
    private ArrayList<Double> finishedOrdersAverages = new ArrayList<>();
    private double ultimateLowestOrdersFinished = -1;
    private double ultimateHighestOrdersFinished = -1;

    /**
     * Order stats
     */
    private double quickestOrderAverage;
    private double slowestOrderAverage;
    private double averageOrderAverage;
    private double ultimateQuickestOrder = -1;
    private double ultimateSlowestOrder = -1;

    /**
     * Robot stats
     */
    private double averageDistanceTraveledAverage;
    private double shortestDistanceTraveledAverage;
    private double longestDistanceTraveledAverage;
    private double averageIdleTimeAverage;
    private double leastIdleTimeAverage;
    private double mostIdleTimeAverage;
    private double averageDeliveriesAverage;
    private double fewestDeliveriesAverage;
    private double mostDeliveriesAverage;
    private double ultimateMostIdleTime;
    private double ultimateLeastIdleTime;
    private int ultimateLongestDistanceTraveled = -1;
    private int ultimateShortestDistanceTraveled = -1;
    private int ultimateMostDeliveries = -1;
    private int ultimateFewestDeliveries = -1;

    public double getUltimateLowestOrdersFinished() {
        return ultimateLowestOrdersFinished;
    }

    public double getUltimateHighestOrdersFinished() {
        return ultimateHighestOrdersFinished;
    }

    public void addAvailableProductsAverage(Double d){
        availableProductsLeftAverages.add(d);
    }

    public void addOrdersInQueueAverage(Double d){
        ordersInQueueAverages.add(d);
    }

    public void addFinishedOrdersAverage(double d){
        if(ultimateHighestOrdersFinished == -1) ultimateHighestOrdersFinished = d;
        if(ultimateLowestOrdersFinished == -1) ultimateLowestOrdersFinished = d;
        if(ultimateHighestOrdersFinished < d) ultimateHighestOrdersFinished = d;
        if(ultimateLowestOrdersFinished > d) ultimateLowestOrdersFinished = d;

        finishedOrdersAverages.add(d);
    }

    public double getOrdersFinishedAverage(){
        double sum = 0;
        for(Double d : finishedOrdersAverages){
            sum += d;
        }
        return sum / seedsVisited;
    }

    public void addOrdersPerMinuteAverage(Double d){
        if(ultimateLowestOrderPerMinute == -1) ultimateLowestOrderPerMinute = d;
        if(ultimateHighestOrdersPerMinute == -1) ultimateHighestOrdersPerMinute = d;
        if(ultimateLowestOrderPerMinute > d) ultimateLowestOrderPerMinute = d;
        if(ultimateHighestOrdersPerMinute < d ) ultimateHighestOrdersPerMinute = d;

        ordersPerMinuteAverages.add(d);
    }

    public double getOrdersPerMinuteAverage(){
        double sum = 0;
        for(Double d : ordersPerMinuteAverages){
            sum += d;
        }
        return sum / seedsVisited;
    }

    public double getAvailableProductsLeftAverage(){
        double sum = 0;
        for(Double d : availableProductsLeftAverages){
            sum += d;
        }
        return sum / seedsVisited;
    }

    public double getOrdersInQueueAverage(){
        double sum = 0;
        for(Double d : ordersInQueueAverages){
            sum += d;
        }
        return sum / seedsVisited;
    }

    public void incrementSeedsVisited(){
        seedsVisited++;
    }

    public double getUltimateLowestOrderPerMinute() {
        return ultimateLowestOrderPerMinute;
    }


    public double getUltimateHighestOrdersPerMinute() {
        return ultimateHighestOrdersPerMinute;
    }

    public double getQuickestOrderAverage() {
        return quickestOrderAverage;
    }

    public void setQuickestOrderAverage(double quickestOrderAverage) {
        this.quickestOrderAverage = quickestOrderAverage;
    }

    public double getSlowestOrderAverage() {
        return slowestOrderAverage;
    }

    public void setSlowestOrderAverage(double slowestOrderAverage) {
        this.slowestOrderAverage = slowestOrderAverage;
    }

    public double getAverageOrderAverage() {
        return averageOrderAverage;
    }

    public void setAverageOrderAverage(double averageOrderAverage) {
        this.averageOrderAverage = averageOrderAverage;
    }

    public double getUltimateQuickestOrder() {
        return ultimateQuickestOrder;
    }

    public void setUltimateQuickestOrder(double ultimateQuickestOrder) {
        this.ultimateQuickestOrder = ultimateQuickestOrder;
    }

    public double getUltimateSlowestOrder() {
        return ultimateSlowestOrder;
    }

    public void setUltimateSlowestOrder(double ultimateSlowestOrder) {
        this.ultimateSlowestOrder = ultimateSlowestOrder;
    }

    public double getAverageDistanceTraveledAverage() {
        return averageDistanceTraveledAverage;
    }

    public void setAverageDistanceTraveledAverage(double averageDistanceTraveledAverage) {
        this.averageDistanceTraveledAverage = averageDistanceTraveledAverage;
    }

    public double getShortestDistanceTraveledAverage() {
        return shortestDistanceTraveledAverage;
    }

    public void setShortestDistanceTraveledAverage(double shortestDistanceTraveledAverage) {
        this.shortestDistanceTraveledAverage = shortestDistanceTraveledAverage;
    }

    public double getLongestDistanceTraveledAverage() {
        return longestDistanceTraveledAverage;
    }

    public void setLongestDistanceTraveledAverage(double longestDistanceTraveledAverage) {
        this.longestDistanceTraveledAverage = longestDistanceTraveledAverage;
    }

    public double getLeastIdleTimeAverage() {
        return leastIdleTimeAverage;
    }

    public void setLeastIdleTimeAverage(double leastIdleTimeAverage) {
        this.leastIdleTimeAverage = leastIdleTimeAverage;
    }

    public double getMostIdleTimeAverage() {
        return mostIdleTimeAverage;
    }

    public void setMostIdleTimeAverage(double mostIdleTimeAverage) {
        this.mostIdleTimeAverage = mostIdleTimeAverage;
    }

    public double getAverageIdleTimeAverage() {
        return averageIdleTimeAverage;
    }

    public void setAverageIdleTimeAverage(double averageIdleTimeAverage) {
        this.averageIdleTimeAverage = averageIdleTimeAverage;
    }

    public double getFewestDeliveriesAverage() {
        return fewestDeliveriesAverage;
    }

    public void setFewestDeliveriesAverage(double fewestDeliveriesAverage) {
        this.fewestDeliveriesAverage = fewestDeliveriesAverage;
    }

    public double getMostDeliveriesAverage() {
        return mostDeliveriesAverage;
    }

    public void setMostDeliveriesAverage(double mostDeliveriesAverage) {
        this.mostDeliveriesAverage = mostDeliveriesAverage;
    }

    public double getAverageDeliveriesAverage() {
        return averageDeliveriesAverage;
    }

    public void setAverageDeliveriesAverage(double averageDeliveriesAverage) {
        this.averageDeliveriesAverage = averageDeliveriesAverage;
    }

    public double getUltimateMostIdleTime() {
        return ultimateMostIdleTime;
    }

    public void setUltimateMostIdleTime(double ultimateMostIdleTime) {
        this.ultimateMostIdleTime = ultimateMostIdleTime;
    }

    public double getUltimateLeastIdleTime() {
        return ultimateLeastIdleTime;
    }

    public void setUltimateLeastIdleTime(double ultimateLeastIdleTime) {
        this.ultimateLeastIdleTime = ultimateLeastIdleTime;
    }

    public int getUltimateLongestDistanceTraveled() {
        return ultimateLongestDistanceTraveled;
    }

    public void setUltimateLongestDistanceTraveled(int ultimateLongestDistanceTraveled) {
        this.ultimateLongestDistanceTraveled = ultimateLongestDistanceTraveled;
    }

    public int getUltimateShortestDistanceTraveled() {
        return ultimateShortestDistanceTraveled;
    }

    public void setUltimateShortestDistanceTraveled(int ultimateShortestDistanceTraveled) {
        this.ultimateShortestDistanceTraveled = ultimateShortestDistanceTraveled;
    }

    public int getUltimateMostDeliveries() {
        return ultimateMostDeliveries;
    }

    public void setUltimateMostDeliveries(int ultimateMostDeliveries) {
        this.ultimateMostDeliveries = ultimateMostDeliveries;
    }

    public int getUltimateFewestDeliveries() {
        return ultimateFewestDeliveries;
    }

    public void setUltimateFewestDeliveries(int ultimateFewestDeliveries) {
        this.ultimateFewestDeliveries = ultimateFewestDeliveries;
    }
}
