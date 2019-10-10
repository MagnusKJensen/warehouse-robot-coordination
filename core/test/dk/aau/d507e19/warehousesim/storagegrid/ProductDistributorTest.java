package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
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

    @Test
    public void calculateProductsPerSKU01() {
        float[][] distribution = {{20,80},{80,20}};

        ProductDistributor.setSKUDistribution(distribution);
        int[][] products =  ProductDistributor.calculateProductsPerSKU();
        assertTrue(products.length == WarehouseSpecs.SKUs);
    }

    @Test
    public void calculateProductsPerSKU02() {
        float[][] distribution = {{20, 50}, {10,30}, {70,20}};

        ProductDistributor.setSKUDistribution(distribution);
        int[][] products =  ProductDistributor.calculateProductsPerSKU();
        assertTrue(products.length == WarehouseSpecs.SKUs);
    }

    @Test
    public void calculateProductsPerSKU03() {
        float[][] distribution = {{20, 50}, {10,30}, {70,20}};
        ProductDistributor.setSKUDistribution(distribution);

        int[][] products =  ProductDistributor.calculateProductsPerSKU();

        int numberOfProducts = 0;
        for(int i = 0; i < products.length; ++i){
            numberOfProducts += products[i][1];
        }

        assertTrue(numberOfProducts == WarehouseSpecs.productsInStock);
    }
}