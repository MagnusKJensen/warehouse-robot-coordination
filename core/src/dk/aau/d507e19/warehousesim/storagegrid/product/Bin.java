package dk.aau.d507e19.warehousesim.storagegrid.product;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Bin {
    ArrayList<Product> products = new ArrayList<>();

    public Bin() {
    }

    public Bin(ArrayList<Product> products) {
        addProducts(products);
    }

    public void addProducts(ArrayList<Product> newProducts){
        // Go through all products
        for(Product newProd : newProducts){
            addProduct(newProd);
        }
    }

    public void addProduct(Product newProd){
        // If the number of SKUs per bin is already full, and the new product has a new SKU
        if(getSKUs().size() == WarehouseSpecs.SKUsPerBin && !hasSKU(newProd.getSKU())){
            throw new IllegalArgumentException("Cannot add more SKUs to bin. Already have '" + getSKUs().size()
                    + "' out of '" + WarehouseSpecs.SKUsPerBin + "'");
        }
        // If the bin is already full of products
        else if(products.size() == WarehouseSpecs.productsPerBin){
            throw new IllegalArgumentException("Cannot add more products to bin. Already has '" + products.size()
                    + "' out of '" + WarehouseSpecs.productsPerBin + "'");
        } else {
            products.add(newProd);
        }
    }

    private ArrayList<SKU> getSKUs(){
        ArrayList<SKU> SKUs = new ArrayList<>();
        for (Product prod : products) {
            if(!SKUs.contains(prod.SKU)){
                SKUs.add(prod.SKU);
            }
        }
        return SKUs;
    }

    public boolean isFull(){
        return products.size() == WarehouseSpecs.productsPerBin;
    }

    public boolean hasSKU(SKU sku){
        for (Product prod : products) {
            if(prod.SKU.EAN.equals(sku.EAN)) return true;
        }
        return false;
    }

    public boolean hasRoomForMoreSKUs(){
        ArrayList<SKU> SKUs = new ArrayList<>();
        for (Product prod : products) {
            if(!SKUs.contains(prod.SKU)) SKUs.add(prod.SKU);
        }

        if(SKUs.size() < 9) return true;

        return false;
    }

    @Override
    public String toString() {
        if(products.isEmpty()){
            return "Bin{}";
        } else {
            String s;
            s = "Bin{\n";
            for (Product prod : products) {
                s = s.concat(prod + "\n");
            }
            s = s.concat("}");
            return s;
        }
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
