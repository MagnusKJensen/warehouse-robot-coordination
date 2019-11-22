package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ProductDistributorTest {

    @BeforeClass
    public static void init(){
        RunConfigurator.setDefaultRunConfiguration();
    }


    @Test
    public void isValidDistribution01() {
        double[][] distribution = {{20,80},{80,20}};

        ProductDistributor productDistributor = new ProductDistributor(Simulation.getWarehouseSpecs());

        assertTrue(productDistributor.isValidDistribution(distribution));
    }

    @Test
    public void isValidDistribution02() {
        double[][] distribution = {{80,20},{20,80}};

        ProductDistributor productDistributor = new ProductDistributor(Simulation.getWarehouseSpecs());

        assertTrue(productDistributor.isValidDistribution(distribution));
    }

    @Test
    public void calculateProductsPerSKU01() {
        double[][] distribution = {{20,80},{80,20}};

        ProductDistributor productDistributor = new ProductDistributor(Simulation.getWarehouseSpecs());

        int[][] products =  productDistributor.calculateProductsPerSKU(Simulation.getWarehouseSpecs().SKUs, Simulation.getWarehouseSpecs().productsInStock, distribution);
        assertEquals(products.length, Simulation.getWarehouseSpecs().SKUs);
    }

    @Test
    public void calculateProductsPerSKU02() {
        double[][] distribution = {{20, 50}, {10,30}, {70,20}};

        ProductDistributor productDistributor = new ProductDistributor(Simulation.getWarehouseSpecs());

        int[][] products =  productDistributor.calculateProductsPerSKU(Simulation.getWarehouseSpecs().SKUs, Simulation.getWarehouseSpecs().productsInStock, distribution);
        assertEquals(products.length, Simulation.getWarehouseSpecs().SKUs);
    }

    @Test
    public void calculateProductsPerSKU03() {
        double[][] distribution = {{20, 50}, {10,30}, {70,20}};

        ProductDistributor productDistributor = new ProductDistributor(Simulation.getWarehouseSpecs());
        int[][] products =  productDistributor.calculateProductsPerSKU(Simulation.getWarehouseSpecs().SKUs, Simulation.getWarehouseSpecs().productsInStock, distribution);

        int numberOfProducts = 0;
        for(int i = 0; i < products.length; ++i){
            numberOfProducts += products[i][1];
        }

        assertEquals(numberOfProducts, Simulation.getWarehouseSpecs().productsInStock);
    }

    @Test
    public void calculateProductsPerSKU04() {
        double[][] distribution = {{10, 77}, {90, 23}};

        ProductDistributor productDistributor = new ProductDistributor(Simulation.getWarehouseSpecs());
        int[][] products =  productDistributor.calculateProductsPerSKU(Simulation.getWarehouseSpecs().SKUs, Simulation.getWarehouseSpecs().productsInStock, distribution);

        int numberOfProducts = 0;
        for(int i = 0; i < products.length; ++i){
            numberOfProducts += products[i][1];
        }

        assertEquals(numberOfProducts, Simulation.getWarehouseSpecs().productsInStock);
    }
}