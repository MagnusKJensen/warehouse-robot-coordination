package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;

public class ProductDistributor {
    private static final int totalSKUsInWarehouse = WarehouseSpecs.SKUs;
    private static final int numberOfProducts = WarehouseSpecs.productsInStock;
    private static final int numberOfBins = WarehouseSpecs.wareHouseWidth * WarehouseSpecs.wareHouseHeight;
    private static final int productsPerBin = WarehouseSpecs.productsPerBin;
    private static final int SKUsPerBin = WarehouseSpecs.SKUsPerBin;
    private static float[][] SKUDistribution = WarehouseSpecs.skuDistribution;

    public static void distributeProducts(StorageGrid grid){
        int[][] products =  generateProducts();

        int totalProducts = 0;
        for(int i = 0; i < products.length; i++){
            //System.out.println("SKU: " + products[i][0] + ", number of products: " + products[i][1]);
            totalProducts += products[i][1];
        }
        System.out.println("Total products: " + totalProducts);
    }

    private static int[][] generateProducts() {
        if(!isValidDistribution()) throw new IllegalArgumentException("Distribution or turnover does not equal 100%");

        // A list of all SKUs. Index 0 is SKU name as int. Index 1 is number of products for the given SKU.
        int[][] products = new int[totalSKUsInWarehouse][2];

        int numberOfDistributions = SKUDistribution.length;
        System.out.println("Number of distributions: " + numberOfDistributions);

        int currentSKU = 0;
        int nextSKUName = 0;
        int shouldHaveExtraProduct = 0;
        // Run through other SKU distributions
        for(int i = 0; i < numberOfDistributions; ++i){
            System.out.println("Distribution number:  " + i);
            // How many SKUs in this category (Total SKUs / SKU turnover in percent)
            int SKUsInCategory = (int)(totalSKUsInWarehouse * (SKUDistribution[i][0] / 100));
            System.out.println("SKU distribution:" + SKUsInCategory);
            // Products in this SKU category (Total products / SKU category turnover)
            int productsInSKUCat = (int) (numberOfProducts * (SKUDistribution[i][1] / 100));
            System.out.println("ProductsInSKUCat: " + productsInSKUCat);
            // How many products does each SKU in this category have? (products in this SKU / SKUs in category)
            int productPerSKU = productsInSKUCat / SKUsInCategory;
            System.out.println("productPerSKU: " + productPerSKU);

            // If the products did not divide equally between the SKUs
            if(productPerSKU * SKUsInCategory != productsInSKUCat){
                int difference = Math.abs((productPerSKU * SKUsInCategory) - productsInSKUCat);
                System.out.println("Difference: " + difference);
                shouldHaveExtraProduct = difference;
            }

            // Fill in SKU name and number of products for each SKU
            for(int j = nextSKUName; j < SKUsInCategory + currentSKU; j++){
                // Give SKU a name in int
                products[j][0] = nextSKUName;
                // Assign number of products for this SKU
                if(shouldHaveExtraProduct != 0) {
                    products[j][1] = productPerSKU + 1;
                    shouldHaveExtraProduct -= 1;
                }
                else products[j][1] = productPerSKU;

                System.out.println("SKU: " + products[j][0] + ", number of products: " + products[j][1]);
                nextSKUName++;
            }
            currentSKU = nextSKUName;
        }

        fillTheRest(products, currentSKU);

        return products;
    }

    private static void fillTheRest(int[][] products, int nextSKU) {
        int missingSKUs = totalSKUsInWarehouse - nextSKU - 1;
        int missingProducts = numberOfProducts;
        for(int i = 0; i < totalSKUsInWarehouse; ++i){
            missingProducts -= products[i][1];
        }

        System.out.println("Missing SKUs: " + missingSKUs);
        System.out.println("Missing products: " + missingProducts);
    }

    public static boolean isValidDistribution() {
        int SKUSum = 0;
        int turnoverSum = 0;

        for(int i = 0; i < SKUDistribution.length; ++i){
            SKUSum += SKUDistribution[i][0];
            turnoverSum += SKUDistribution[i][1];
        }

        return SKUSum == 100 && turnoverSum == 100;
    }

    public static void setSKUDistribution(float[][] SKUDistribution) {
        ProductDistributor.SKUDistribution = SKUDistribution;
    }
}
