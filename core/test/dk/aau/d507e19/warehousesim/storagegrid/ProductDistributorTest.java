package dk.aau.d507e19.warehousesim.storagegrid;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProductDistributorTest {

    @Test
    public void isValidDistribution01() {
        float[][] distribution = {{20,80},{80,20}};

        ProductDistributor.setSKUDistribution(distribution);
        assertTrue(ProductDistributor.isValidDistribution());
    }

    @Test
    public void isValidDistribution02() {
        float[][] distribution = {{20,80},{80,20}};

        ProductDistributor.setSKUDistribution(distribution);
        assertTrue(ProductDistributor.isValidDistribution());
    }
}