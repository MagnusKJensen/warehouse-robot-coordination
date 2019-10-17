package dk.aau.d507e19.warehousesim.storagegrid.product;

public class SKU {
    String EAN;

    public SKU(String EAN) {
        this.EAN = EAN;
    }

    @Override
    public String toString() {
        return "{EAN='" + EAN + '\'' + "}";
    }
}
