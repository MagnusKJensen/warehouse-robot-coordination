package dk.aau.d507e19.warehousesim.storagegrid.product;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BinTest {

    @Test (expected = IllegalArgumentException.class)
    public void addProducts01() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < WarehouseSpecs.SKUsPerBin + 1; ++i){
            myProducts.add(new Product(new SKU("SKU" + i), 123));
        }

        // Should throw IllegalArgumentException, since one more SKU is added, than the bin can fit.
        bin.addProducts(myProducts);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProducts02() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < WarehouseSpecs.productsPerBin + 1; ++i){
            myProducts.add(new Product(new SKU("SKU"), 123));
        }

        // Should throw IllegalArgumentException, since one more product is added, than the bin can fit.
        bin.addProducts(myProducts);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProduct01() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < WarehouseSpecs.productsPerBin; ++i){
            myProducts.add(new Product(new SKU("SKU"), 123));
        }
        bin.addProducts(myProducts);

        // Should throw IllegalArgumentException, since one more product is added, than the bin can fit.
        bin.addProduct(new Product(new SKU("SKU"), 123));
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProduct02() {
        Bin bin = new Bin();

        ArrayList<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < WarehouseSpecs.SKUsPerBin; ++i){
            myProducts.add(new Product(new SKU("SKU" + 1), 123));
        }
        bin.addProducts(myProducts);

        // Should throw IllegalArgumentException, since one more SKU is added, than the bin can fit.
        bin.addProduct(new Product(new SKU("EXTRA SKU"), 123));
    }
}