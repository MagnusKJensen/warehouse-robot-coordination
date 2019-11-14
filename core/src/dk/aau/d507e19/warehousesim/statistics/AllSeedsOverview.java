package dk.aau.d507e19.warehousesim.statistics;

import java.util.ArrayList;

public class AllSeedsOverview {
    private int seedsVisited = 0;

    public void incrementSeedsVisited(){
        seedsVisited++;
    }
    /** * * * * * * * * * * * * * * * * * * * * * *
     *              General stats                 *
     ** * * * * * * * * * * * * * * * * * * * * * */
    private ArrayList<Double> availableProductsLeftAverages = new ArrayList<>();
    private ArrayList<Double> ordersInQueueAverages = new ArrayList<>();
    private ArrayList<Double> ordersPerMinuteAverages = new ArrayList<>();
    private double ultimateLowestOrderPerMinute = -1;
    private double ultimateHighestOrdersPerMinute = -1;
    private ArrayList<Double> finishedOrdersAverages = new ArrayList<>();
    private double ultimateLowestOrdersFinished = -1;
    private double ultimateHighestOrdersFinished = -1;

    public double getUltimateLowestOrdersFinished() {
        return ultimateLowestOrdersFinished;
    }

    public double getUltimateHighestOrdersFinished() {
        return ultimateHighestOrdersFinished;
    }

    public void addAvailableProductsAverage(double d){
        availableProductsLeftAverages.add(d);
    }

    public void addOrdersInQueueAverage(double d){
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
        return getAverageValue(finishedOrdersAverages);
    }

    public void addOrdersPerMinuteAverage(double d){
        if(ultimateLowestOrderPerMinute == -1) ultimateLowestOrderPerMinute = d;
        if(ultimateHighestOrdersPerMinute == -1) ultimateHighestOrdersPerMinute = d;
        if(ultimateLowestOrderPerMinute > d) ultimateLowestOrderPerMinute = d;
        if(ultimateHighestOrdersPerMinute < d ) ultimateHighestOrdersPerMinute = d;

        ordersPerMinuteAverages.add(d);
    }

    public double getOrdersPerMinuteAverage(){
        return getAverageValue(ordersPerMinuteAverages);
    }

    public double getAvailableProductsLeftAverage(){
        return getAverageValue(availableProductsLeftAverages);
    }

    public double getOrdersInQueueAverage(){
        return getAverageValue(ordersInQueueAverages);
    }

    /** * * * * * * * * * * * * * * * * * * * * * *
     *              Order stats                   *
     ** * * * * * * * * * * * * * * * * * * * * * */
    private ArrayList<Double> quickestOrders = new ArrayList<>();
    private ArrayList<Double> slowestOrders = new ArrayList<>();
    private ArrayList<Double> averageOrders = new ArrayList<>();
    private double ultimateQuickestOrder = -1;
    private double ultimateSlowestOrder = -1;

    public void addAverageOrder(double d){
        averageOrders.add(d);
    }

    public double getAverageOrderAverage(){
        return getAverageValue(averageOrders);
    }

    public void addQuickestOrder(double d){
        if(ultimateQuickestOrder == -1) ultimateQuickestOrder = d;
        if(ultimateSlowestOrder == -1) ultimateSlowestOrder = d;
        if(ultimateQuickestOrder > d) ultimateQuickestOrder = d;
        if(ultimateSlowestOrder < d) ultimateSlowestOrder = d;

        quickestOrders.add(d);
    }

    public double getSlowestOrderAverage(){
        return getAverageValue(slowestOrders);
    }

    public double getQuickestOrderAverage(){
        return getAverageValue(quickestOrders);
    }

    public void addSlowestOrder(double d){
        if(ultimateQuickestOrder == -1) ultimateQuickestOrder = d;
        if(ultimateSlowestOrder == -1) ultimateSlowestOrder = d;
        if(ultimateQuickestOrder > d) ultimateQuickestOrder = d;
        if(ultimateSlowestOrder < d) ultimateSlowestOrder = d;

        slowestOrders.add(d);
    }


    /** * * * * * * * * * * * * * * * * * * * * * *
     *              Robot stats                   *
     ** * * * * * * * * * * * * * * * * * * * * * */
    private ArrayList<Double> averageDistanceTraveledAverages = new ArrayList<>();
    private ArrayList<Double> shortestDistanceTraveled = new ArrayList<>();
    private ArrayList<Double> longestDistanceTraveledAverages = new ArrayList<>();
    private ArrayList<Double> averageIdleTimeAverages = new ArrayList<>();
    private ArrayList<Double> leastIdleTimeAverages = new ArrayList<>();
    private ArrayList<Double> mostIdleTimeAverages = new ArrayList<>();
    private ArrayList<Double> averageDeliveriesAverages = new ArrayList<>();
    private ArrayList<Integer> fewestDeliveries = new ArrayList<>();
    private ArrayList<Integer> mostDeliveries = new ArrayList<>();
    private double ultimateMostIdleTime = -1;
    private double ultimateLeastIdleTime = -1;
    private double ultimateLongestDistanceTraveled = -1;
    private double ultimateShortestDistanceTraveled = -1;
    private int ultimateMostDeliveries = -1;
    private int ultimateFewestDeliveries = -1;

    public double getMostDeliveriesAverage(){
        return getAverageValueInt(mostDeliveries);
    }

    public void addMostDeliveries(int i){
        if(ultimateFewestDeliveries == -1) ultimateFewestDeliveries = i;
        if(ultimateMostDeliveries == -1) ultimateFewestDeliveries = i;
        if(ultimateFewestDeliveries > i) ultimateFewestDeliveries = i;
        if(ultimateMostDeliveries < i) ultimateMostDeliveries = i;

        mostDeliveries.add(i);
    }

    public double getFewestDeliveriesAverage(){
        return getAverageValueInt(fewestDeliveries);
    }

    public void addFewestDeliveries(int i){
        if(ultimateFewestDeliveries == -1) ultimateFewestDeliveries = i;
        if(ultimateMostDeliveries == -1) ultimateFewestDeliveries = i;
        if(ultimateFewestDeliveries > i) ultimateFewestDeliveries = i;
        if(ultimateMostDeliveries < i) ultimateMostDeliveries = i;

        fewestDeliveries.add(i);
    }

    public double getAverageDeliveriesAverage(){
        return getAverageValue(averageDeliveriesAverages);
    }

    public void addAverageDeliveriesAverage(double d){
        averageDeliveriesAverages.add(d);
    }

    public double getMostIdleTimeAverage(){
        return getAverageValue(mostIdleTimeAverages);
    }

    public void addMostIdleTime(double d){
        if(ultimateMostIdleTime == -1) ultimateMostIdleTime = d;
        if(ultimateLeastIdleTime == -1) ultimateLeastIdleTime = d;
        if(ultimateMostIdleTime < d) ultimateMostIdleTime = d;
        if(ultimateLeastIdleTime > d) ultimateLeastIdleTime = d;

        mostIdleTimeAverages.add(d);
    }

    public void addLeastIdleTime(double d){
        if(ultimateMostIdleTime == -1) ultimateMostIdleTime = d;
        if(ultimateLeastIdleTime == -1) ultimateLeastIdleTime = d;
        if(ultimateMostIdleTime < d) ultimateMostIdleTime = d;
        if(ultimateLeastIdleTime > d) ultimateLeastIdleTime = d;

        leastIdleTimeAverages.add(d);
    }

    public double getLeastIdleTimeAverage(){
        return getAverageValue(leastIdleTimeAverages);
    }

    public double getAverageIdleTimeAverage(){
        return getAverageValue(averageIdleTimeAverages);
    }

    public void addAverageIdleTimeAverage(double d){
        averageIdleTimeAverages.add(d);
    }

    public double getLongestDistanceTraveledAverage(){
        return getAverageValue(longestDistanceTraveledAverages);
    }

    public void addLongestDistanceTraveled(double d){
        if(ultimateLongestDistanceTraveled == -1) ultimateLongestDistanceTraveled = d;
        if(ultimateShortestDistanceTraveled == -1) ultimateShortestDistanceTraveled = d;
        if(ultimateLongestDistanceTraveled < d) ultimateLongestDistanceTraveled = d;
        if(ultimateShortestDistanceTraveled > d) ultimateShortestDistanceTraveled = d;

        longestDistanceTraveledAverages.add(d);
    }

    public double getShortestDistanceTraveledAverage(){
        return this.getAverageValue(shortestDistanceTraveled);
    }

    public void addShortestDistanceTraveled(double d){
        if(ultimateLongestDistanceTraveled == -1) ultimateLongestDistanceTraveled = d;
        if(ultimateShortestDistanceTraveled == -1) ultimateShortestDistanceTraveled = d;
        if(ultimateLongestDistanceTraveled < d) ultimateLongestDistanceTraveled = d;
        if(ultimateShortestDistanceTraveled > d) ultimateShortestDistanceTraveled = d;

        shortestDistanceTraveled.add(d);
    }

    public void addAverageDistanceTraveled(double d){
        averageDistanceTraveledAverages.add(d);
    }

    public double getUltimateLowestOrderPerMinute() {
        return ultimateLowestOrderPerMinute;
    }


    public double getUltimateHighestOrdersPerMinute() {
        return ultimateHighestOrdersPerMinute;
    }

    public double getUltimateQuickestOrder() {
        return ultimateQuickestOrder;
    }

    public double getUltimateSlowestOrder() {
        return ultimateSlowestOrder;
    }

    public double getAverageDistanceTraveledAverage() {
        return getAverageValue(averageDistanceTraveledAverages);
    }

    public double getUltimateMostIdleTime() {
        return ultimateMostIdleTime;
    }

    public double getUltimateLeastIdleTime() {
        return ultimateLeastIdleTime;
    }


    public double getUltimateLongestDistanceTraveled() {
        return ultimateLongestDistanceTraveled;
    }

    public double getUltimateShortestDistanceTraveled() {
        return ultimateShortestDistanceTraveled;
    }

    public int getUltimateMostDeliveries() {
        return ultimateMostDeliveries;
    }

    public int getUltimateFewestDeliveries() {
        return ultimateFewestDeliveries;
    }

    private double getAverageValueInt(ArrayList<Integer> list){
        double sum = 0;
        for(Integer i : list){
            sum += i;
        }
        return sum / seedsVisited;
    }

    private double getAverageValue(ArrayList<Double> list){
        double sum = 0;
        for(Double d : list){
            sum += d;
        }
        return sum / seedsVisited;
    }
}
