package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;

import java.util.ArrayList;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;

public class StorageGrid {

    private final Tile[][] tiles;
    private final int width, height;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private ArrayList<GridCoordinate> pickerPoints = new ArrayList<>();
    private Simulation simulation;

    public StorageGrid(int width, int height, Simulation simulation){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.simulation = simulation;
        generatePickerPoints();
        fillGrid();
    }

    private void generatePickerPoints() {
        int[][] pickers = WarehouseSpecs.pickerPoints;

        for(int i = 0; i < pickers.length; ++i){
            GridCoordinate cord = new GridCoordinate(pickers[i][0], pickers[i][1]);
            if(pickerPoints.contains(cord))
                throw new RuntimeException("Picker point already present at (" + cord.getX() + "," + cord.getY() +
                        "). Cannot have two picker points at the same tile");
            else pickerPoints.add(new GridCoordinate(pickers[i][0], pickers[i][1]));
        }
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                if(isAPickerPoint(x,y)) tiles[x][y] = new PickerTile(x,y);
                else tiles[x][y] = new BinTile(x, y);
            }
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch){
        // TODO: 30/09/2019 Adapt so that it only renders tiles within view
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                tiles[x][y].render(shapeRenderer, batch);
            }
        }
    }


    public void renderPathOverlay(ArrayList<Reservation> reservations, ShapeRenderer shapeRenderer){
        for(Reservation reservation : reservations){
            if(reservation.getTimeFrame().isWithinTimeFrame(simulation.getTimeInTicks()))
            tiles[reservation.getGridCoordinate().getX()][reservation.getGridCoordinate().getY()].renderOverlay(shapeRenderer);
        }

    }


    public Tile getTile(int x, int y){
        return tiles[x][y];
    }

    public boolean isAPickerPoint(int x, int y){
        for (GridCoordinate picker: pickerPoints) {
            if(picker.getX() == x && picker.getY() == y){
                return true;
            }
        }
        return false;
    }

}
