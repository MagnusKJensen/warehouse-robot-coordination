package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class OpenListSorterTest {

    OpenListSorter openListSorter;

    @Test
    public void compare() {
        AStarTile aStarTileOne = new AStarTile(1, 2);
        aStarTileOne.calculateG(0);
        aStarTileOne.calculateH(1, 1);
        aStarTileOne.calculateF();
        AStarTile aStarTileTwo = new AStarTile(1, 1);
        aStarTileTwo.calculateG(1);
        aStarTileTwo.calculateH(1, 1);
        aStarTileTwo.calculateF();

        ArrayList<AStarTile> testListOfTiles = new ArrayList<>();
        testListOfTiles.add(aStarTileOne);
        testListOfTiles.add(aStarTileTwo);

        testListOfTiles.sort(new OpenListSorter());
        assertEquals(testListOfTiles.get(0), aStarTileOne);
    }
}
