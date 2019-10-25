package dk.aau.d507e19.warehousesim.storagegrid.product;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(SKU, product.SKU);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SKU);
    }
}
