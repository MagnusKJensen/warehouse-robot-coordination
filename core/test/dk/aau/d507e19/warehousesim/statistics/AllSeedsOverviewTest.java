package dk.aau.d507e19.warehousesim.statistics;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AllSeedsOverviewTest {

    /**
     * General stats
     */
    // private ArrayList<Double> availableProductsLeftAverages = new ArrayList<>();
    // private ArrayList<Double> ordersInQueueAverages = new ArrayList<>();
    // private ArrayList<Double> ordersPerMinuteAverages = new ArrayList<>();
    // private double ultimateLowestOrderPerMinute = -1;
    // private double ultimateHighestOrdersPerMinute = -1;
    // private ArrayList<Double> finishedOrdersAverages = new ArrayList<>();
    // private double ultimateLowestOrdersFinished = -1;
    // private double ultimateHighestOrdersFinished = -1;

    @Test
    public void testAvailableProductsLeftAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addAvailableProductsAverage(5);
        overview.addAvailableProductsAverage(15);

        assertTrue(areDoublesEqual(10, overview.getAvailableProductsLeftAverage()));
    }

    @Test
    public void testOrdersInQueueAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addOrdersInQueueAverage(5);
        overview.addOrdersInQueueAverage(15);

        assertTrue(areDoublesEqual(10, overview.getOrdersInQueueAverage()));
    }

    @Test
    public void testOrdersPerMinuteAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addOrdersPerMinuteAverage(5);
        overview.addOrdersPerMinuteAverage(15);

        assertTrue(areDoublesEqual(10, overview.getOrdersPerMinuteAverage()));
    }

    @Test
    public void testFinishedOrdersAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addFinishedOrdersAverage(5);
        overview.addFinishedOrdersAverage(15);

        assertTrue(areDoublesEqual(10, overview.getOrdersFinishedAverage()));
    }

    @Test
    public void testUltimateLowestHighestOrderPerMinute(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addOrdersPerMinuteAverage(5);
        overview.addOrdersPerMinuteAverage(15);

        assertTrue(areDoublesEqual(15, overview.getUltimateHighestOrdersPerMinute()));
        assertTrue(areDoublesEqual(5, overview.getUltimateLowestOrderPerMinute()));
    }


    @Test
    public void testUltimateHighestLowestOrdersFinished(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addFinishedOrdersAverage(5);
        overview.addFinishedOrdersAverage(15);

        assertTrue(areDoublesEqual(15, overview.getUltimateHighestOrdersFinished()));
        assertTrue(areDoublesEqual(5, overview.getUltimateLowestOrdersFinished()));
    }

    /**
     * Order stats
     */
    // private double ultimateQuickestOrder = -1;
    // private double ultimateSlowestOrder = -1;
    // private ArrayList<Double> quickestOrders = new ArrayList<>();
    // private ArrayList<Double> slowestOrders = new ArrayList<>();
    // private ArrayList<Double> averageOrders = new ArrayList<>();

    @Test
    public void testAverageOrdersAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addAverageOrder(5);
        overview.addAverageOrder(15);

        assertTrue(areDoublesEqual(10, overview.getAverageOrderAverage()));
    }

    @Test
    public void testSlowestOrdersAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addSlowestOrder(5);
        overview.addSlowestOrder(15);

        assertTrue(areDoublesEqual(10, overview.getSlowestOrderAverage()));
    }

    @Test
    public void testQuickestOrderAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addQuickestOrder(5);
        overview.addQuickestOrder(15);

        assertTrue(areDoublesEqual(10, overview.getQuickestOrderAverage()));
    }

    @Test
    public void testUltimateQuickestSlowestOrder(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addQuickestOrder(5);
        overview.addSlowestOrder(15);

        assertTrue(areDoublesEqual(15, overview.getUltimateSlowestOrder()));
        assertTrue(areDoublesEqual(5, overview.getUltimateQuickestOrder()));
    }

    /**
     * Robot stats
     */
    // private double ultimateMostIdleTime = -1;
    // private double ultimateLeastIdleTime = -1;
    // private double ultimateLongestDistanceTraveled = -1;
    // private double ultimateShortestDistanceTraveled = -1;
    // private int ultimateMostDeliveries = -1;
    // private int ultimateFewestDeliveries = -1;
    // private ArrayList<Double> averageDistanceTraveledAverages = new ArrayList<>();
    // private ArrayList<Double> shortestDistanceTraveled = new ArrayList<>();
    // private ArrayList<Double> longestDistanceTraveledAverages = new ArrayList<>();
    // private ArrayList<Double> averageIdleTimeAverages = new ArrayList<>();
    // private ArrayList<Double> leastIdleTimeAverages = new ArrayList<>();
    // private ArrayList<Double> mostIdleTimeAverages = new ArrayList<>();
    // private ArrayList<Double> averageDeliveriesAverages = new ArrayList<>();
    // private ArrayList<Integer> fewestDeliveries = new ArrayList<>();
    // private ArrayList<Integer> mostDeliveries = new ArrayList<>();

    @Test
    public void testAverageDistanceTraveledAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addAverageDistanceTraveled(5);
        overview.addAverageDistanceTraveled(15);

        assertTrue(areDoublesEqual(10, overview.getAverageDistanceTraveledAverage()));
    }

    @Test
    public void testShortestDistanceTraveled(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addShortestDistanceTraveled(5);
        overview.addShortestDistanceTraveled(15);

        assertTrue(areDoublesEqual(10, overview.getShortestDistanceTraveledAverage()));
    }

    @Test
    public void testLongestDistanceTraveledAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addLongestDistanceTraveled(5);
        overview.addLongestDistanceTraveled(15);

        assertTrue(areDoublesEqual(10, overview.getLongestDistanceTraveledAverage()));
    }

    @Test
    public void testLeastIdleTimeAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addLeastIdleTime(5);
        overview.addLeastIdleTime(15);

        assertTrue(areDoublesEqual(10, overview.getLeastIdleTimeAverage()));
    }

    @Test
    public void testAverageIdleTimeAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addAverageIdleTimeAverage(5);
        overview.addAverageIdleTimeAverage(15);

        assertTrue(areDoublesEqual(10, overview.getAverageIdleTimeAverage()));
    }

    @Test
    public void testMostIdleTimeAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addMostIdleTime(5);
        overview.addMostIdleTime(15);

        assertTrue(areDoublesEqual(10, overview.getMostIdleTimeAverage()));
    }

    @Test
    public void testAverageDeliveriesAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addAverageDeliveriesAverage(5);
        overview.addAverageDeliveriesAverage(15);

        assertTrue(areDoublesEqual(10, overview.getAverageDeliveriesAverage()));
    }

    @Test
    public void testFewestDeliveries(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addFewestDeliveries(5);
        overview.addFewestDeliveries(15);

        assertTrue(areDoublesEqual(10, overview.getFewestDeliveriesAverage()));
    }

    @Test
    public void testMostDeliveriesAverage(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addMostDeliveries(5);
        overview.addMostDeliveries(15);

        assertTrue(areDoublesEqual(10, overview.getMostDeliveriesAverage()));
    }

    @Test
    public void testUltimateMostIdle(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addMostIdleTime(5);
        overview.addLeastIdleTime(15);

        assertTrue(areDoublesEqual(15, overview.getUltimateMostIdleTime()));
        assertTrue(areDoublesEqual(5, overview.getUltimateLeastIdleTime()));
    }

    @Test
    public void testUltimateShortestLongestDistance(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addLongestDistanceTraveled(5);
        overview.addShortestDistanceTraveled(15);

        assertTrue(areDoublesEqual(15, overview.getUltimateLongestDistanceTraveled()));
        assertTrue(areDoublesEqual(5, overview.getUltimateShortestDistanceTraveled()));
    }

    @Test
    public void testUltimateLowestHighestDeliveries(){
        AllSeedsOverview overview = new AllSeedsOverview();
        overview.incrementSeedsVisited();
        overview.incrementSeedsVisited();

        overview.addMostDeliveries(10);
        overview.addMostDeliveries(15);

        assertEquals(15, overview.getUltimateMostDeliveries());
        assertEquals(10, overview.getUltimateFewestDeliveries());
    }

    boolean areDoublesEqual(double d1, double d2){
        double delta = 0.01;
        if(-delta < d1 - d2 && d1 - d2 < delta){
            return true;
        }
        return false;
    }

}