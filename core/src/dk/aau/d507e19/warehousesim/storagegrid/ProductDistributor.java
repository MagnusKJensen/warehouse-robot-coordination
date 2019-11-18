package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;

import java.util.ArrayList;
import java.util.Random;

public class ProductDistributor {
    private WarehouseSpecs warehouseSpecs;
    private Random random;

    public ProductDistributor(WarehouseSpecs specs) {
        this.warehouseSpecs = specs;
        this.random = new Random(Simulation.RANDOM_SEED);
    }

    public void distributeProducts(StorageGrid grid){
        if(warehouseSpecs.productsPerBin * warehouseSpecs.wareHouseHeight * warehouseSpecs.wareHouseWidth
                < warehouseSpecs.productsInStock) throw new IllegalArgumentException("Too many products for the grid. Please lower productsInStock to be below" +
                "number of binTile * productsPerBin");

        int[][] SKUs = calculateProductsPerSKU(warehouseSpecs.SKUs, warehouseSpecs.productsInStock, warehouseSpecs.skuDistribution);

        ArrayList<Product> allProducts = generateProducts(SKUs);

        grid.setAllProducts(allProducts);

        distributeToGrid(new ArrayList<>(allProducts), grid);
    }

    public void distributeProductsRandomly(StorageGrid grid){
        if(warehouseSpecs.SKUs * warehouseSpecs.wareHouseHeight * warehouseSpecs.wareHouseWidth
                < warehouseSpecs.productsInStock) throw new IllegalArgumentException("The products may not fit in the grid. Please lower productsInStock to be below" +
                "number of binTile * SKUsPerBin");

        int[][] SKUs = calculateProductsPerSKU(warehouseSpecs.SKUs, warehouseSpecs.productsInStock, warehouseSpecs.skuDistribution);

        ArrayList<Product> allProducts = generateProducts(SKUs);

        // Sanity check
        assert(allProducts.size() == warehouseSpecs.productsInStock);

        grid.setAllProducts(allProducts);

        distributeRandomly(new ArrayList<>(allProducts), grid);
    }

    private void distributeRandomly(ArrayList<Product> allProducts, StorageGrid grid) {
        ArrayList<BinTile> nonFullTiles = getAllBinTiles(grid);

        for(BinTile tile : nonFullTiles){
            tile.addBin(new Bin());
        }

        int attempts = 0;
        int MAX_ATTEMPTS = 100000;
        // While some tiles are not full, and more products need to be added
        while(!nonFullTiles.isEmpty() && !allProducts.isEmpty()){
            int nextTile = random.nextInt(nonFullTiles.size());

            attempts++;
            // If the bin already has the SKU or has room for more SKUs
            if(nonFullTiles.get(nextTile).getBin().hasSKU(allProducts.get(0).getSKU())
                    || nonFullTiles.get(nextTile).getBin().hasRoomForMoreSKUs()) {
                nonFullTiles.get(nextTile).getBin().addProduct(allProducts.get(0));
                allProducts.remove(0);
                attempts = 0;
            }

            // If the bin is now full, remove it from the nonFullTiles.
            if(nonFullTiles.get(nextTile).getBin().isFull()){
                nonFullTiles.remove(nextTile);
            }


            if(attempts > MAX_ATTEMPTS) throw new IllegalArgumentException("Could not distribute products after " + attempts +  " attempts. Not enough room." +
                    " Still need " + allProducts.size() +  " product(s) to fit in " + nonFullTiles.size() + " bins");
        }

        if(nonFullTiles.isEmpty() && !allProducts.isEmpty()) throw new IllegalArgumentException("Could not distribute products. Not enough room.");
    }

    private ArrayList<BinTile> getAllBinTiles(StorageGrid grid) {
        ArrayList<BinTile> tiles = new ArrayList<>();

        for(int x = 0; x < warehouseSpecs.wareHouseWidth; ++x){
            for(int y = 0; y < warehouseSpecs.wareHouseHeight; ++y){
                if(grid.getTile(x,y) instanceof BinTile) tiles.add((BinTile) grid.getTile(x,y));
            }
        }

        return tiles;
    }

    private void distributeToGrid(ArrayList<Product> allProducts, StorageGrid grid) {
        Tile tile;
        Bin bin;

        // Run though all tiles in the warehouse
        for(int x = 0; x < warehouseSpecs.wareHouseWidth; ++x){
            for(int y = 0; y < warehouseSpecs.wareHouseHeight; ++y){
                tile = grid.getTile(x,y);
                if(tile instanceof BinTile){
                    // If it does not have a bin, add one
                    if(!((BinTile) tile).hasBin()) ((BinTile) tile).addBin(new Bin());

                    // Start adding products to bin
                    bin = ((BinTile) tile).getBin();
                    // Keep filling the bin
                    while(!bin.isFull() && !allProducts.isEmpty()){
                        // If the bin has the SKU and is not full, add the product.
                        if(bin.hasSKU(allProducts.get(0).getSKU()) && !bin.isFull()) {
                            bin.addProduct(allProducts.get(0));
                            allProducts.remove(0);
                        }
                        // If the bin is not full and has room for more SKUs
                        else if(!bin.isFull() && bin.hasRoomForMoreSKUs()){
                            bin.addProduct(allProducts.get(0));
                            allProducts.remove(0);
                        } else break;
                    }
                }
            }
        }

        if(!allProducts.isEmpty()) throw new RuntimeException("Not enough room for all products." +
                "Still need room for '" + allProducts.size() + "' products.");
    }

    /**
     * Get an ArrayList of all products
     * @param SKUs Array of int arrays. int[i][0] = SKU name of index i. int[i][1] = number of products for index i.
     * @return
     */
    private ArrayList<Product> generateProducts(int[][] SKUs) {
        ArrayList<Product> allProducts = new ArrayList<>();

        for(int i = 0; i < SKUs.length; ++i){
            for(int j = 0; j < SKUs[i][1]; ++j){
                allProducts.add(new Product( new SKU(SKUs[i][0] + "")));
            }
        }

        return allProducts;
    }

    /**
     * Calculate how many products each SKU should have according to the distribution given in WarehouseSpec
     * @return Array of int arrays. int[i][0] = SKU name of index i. int[i][1] = number of products for index i.
     * @param SKUs
     * @param productsInStock
     */
    public int[][] calculateProductsPerSKU(int SKUs, int productsInStock, double[][] distribution) {
        if(!isValidDistribution(distribution)) throw new IllegalArgumentException("Distribution or turnover does not equal 100%");

        // A list of all SKUs. Index 0 is SKU name as int. Index 1 is number of products for the given SKU.
        int[][] products = new int[SKUs][2];

        int numberOfDistributions = distribution.length;

        int currentSKU = 0;
        int nextSKUName = 0;
        int shouldHaveExtraProduct = 0;
        int totalSKUsCounted = 0;
        int totalProductsCounted = 0;
        int SKUsInCategory;
        int productsInSKUCat;
        int productPerSKU;
        // Run through all SKU distributions (See WarehouseSpecs.skuDistribution)
        for(int i = 0; i < numberOfDistributions; ++i){
            // How many SKUs in this category (Total SKUs / SKU turnover in percent)
            // If this is the last distribution take the rest of the SKUs.
            if(i == numberOfDistributions - 1) SKUsInCategory = SKUs - totalSKUsCounted;
            else SKUsInCategory = (int)(SKUs * (distribution[i][0] / 100));


            //Products in this SKU category (Total products / SKU category turnover)
            // If this is the last distribution, take the rest of the products
            if(i == numberOfDistributions - 1) productsInSKUCat = productsInStock - totalProductsCounted;
            else productsInSKUCat = (int) (productsInStock * (distribution[i][1] / 100));

            // How many products does each SKU in this category have? (products in this SKU / SKUs in category)
            productPerSKU = productsInSKUCat / SKUsInCategory;

            // If the products did not divide equally between the SKUs
            if(productPerSKU * SKUsInCategory != productsInSKUCat){
                shouldHaveExtraProduct = Math.abs((productPerSKU * SKUsInCategory) - productsInSKUCat);
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

                nextSKUName++;
                totalSKUsCounted++;
                totalProductsCounted += products[j][1];
            }
            currentSKU = nextSKUName;
        }

        return products;
    }

    public boolean isValidDistribution(double[][] distribution) {
        float SKUSum = 0;
        float turnoverSum = 0;
        float delta = 0.01f;

        for(int i = 0; i < distribution.length; ++i){
            SKUSum += distribution[i][0];
            turnoverSum += distribution[i][1];
        }

        if(Math.abs(SKUSum - 100) > delta) return false;
        if(Math.abs(turnoverSum - 100) > delta) return false;

        return true;
    }
}
