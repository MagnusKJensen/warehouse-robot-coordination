package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(products.length, WarehouseSpecs.SKUs);
    }

    @Test
    public void calculateProductsPerSKU02() {
        float[][] distribution = {{20, 50}, {10,30}, {70,20}};

        ProductDistributor.setSKUDistribution(distribution);
        int[][] products =  ProductDistributor.calculateProductsPerSKU();
        assertEquals(products.length, WarehouseSpecs.SKUs);
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

        assertEquals(numberOfProducts, WarehouseSpecs.productsInStock);
    }
}