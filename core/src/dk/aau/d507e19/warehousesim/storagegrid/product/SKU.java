package dk.aau.d507e19.warehousesim.storagegrid.product;

import java.util.Objects;

public class SKU {
    String EAN;

    public SKU(String EAN) {
        this.EAN = EAN;
    }

    @Override
    public String toString() {
        return "{EAN='" + EAN + '\'' + "}";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SKU sku = (SKU) o;
        return Objects.equals(EAN, sku.EAN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EAN);
    }

    public String getEAN() {
        return EAN;
    }
}
