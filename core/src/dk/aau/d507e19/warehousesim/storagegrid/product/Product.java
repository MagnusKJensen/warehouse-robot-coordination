package dk.aau.d507e19.warehousesim.storagegrid.product;

public class Product {
    SKU SKU;
    int uniqueID;

    public Product(SKU SKU, int uniqueID) {
        this.SKU = SKU;
        this.uniqueID = uniqueID;
    }

    public Product(SKU SKU) {
        this.SKU = SKU;
    }
}
