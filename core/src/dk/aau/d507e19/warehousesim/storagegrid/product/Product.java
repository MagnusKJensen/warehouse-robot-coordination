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

    @Override
    public String toString() {
        return "SKU=" + SKU +
                ", uniqueID=" + uniqueID;
    }

    public SKU getSKU() {
        return SKU;
    }

    public int getUniqueID() {
        return uniqueID;
    }
}
