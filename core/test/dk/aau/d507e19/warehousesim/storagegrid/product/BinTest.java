package dk.aau.d507e19.warehousesim.storagegrid.product;

import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class BinTest {
    @BeforeClass
    public static void init(){
        RunConfigurator.setDefaultRunConfiguration();
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProducts01() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < Simulation.getWarehouseSpecs().SKUsPerBin + 1; ++i){
            myProducts.add(new Product(new SKU("SKU" + i)));
        }

        // Should throw IllegalArgumentException, since one more SKU is added, than the bin can fit.
        bin.addProducts(myProducts);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProducts02() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < Simulation.getWarehouseSpecs().productsPerBin + 1; ++i){
            myProducts.add(new Product(new SKU("SKU")));
        }

        // Should throw IllegalArgumentException, since one more product is added, than the bin can fit.
        bin.addProducts(myProducts);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProduct01() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < Simulation.getWarehouseSpecs().productsPerBin; ++i){
            myProducts.add(new Product(new SKU("SKU")));
        }
        bin.addProducts(myProducts);

        // Should throw IllegalArgumentException, since one more product is added, than the bin can fit.
        bin.addProduct(new Product(new SKU("SKU")));
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProduct02() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < Simulation.getWarehouseSpecs().SKUsPerBin; ++i){
            myProducts.add(new Product(new SKU("SKU" + i)));
        }
        bin.addProducts(myProducts);

        // Should throw IllegalArgumentException, since one more SKU is added, than the bin can fit.
        bin.addProduct(new Product(new SKU("EXTRA SKU")));
    }
}