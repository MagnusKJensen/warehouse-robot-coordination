package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.Test;

import static org.junit.Assert.*;

public class GridBoundsTest {

    @Test
    public void withinGridBounds() {
        GridBounds gridBounds = new GridBounds(0, 0, 15, 15);
        assertTrue(gridBounds.isWithinBounds(new GridCoordinate(0, 0)));
        assertTrue(gridBounds.isWithinBounds(new GridCoordinate(15, 15)));
        assertTrue(gridBounds.isWithinBounds(new GridCoordinate(0, 15)));
        assertTrue(gridBounds.isWithinBounds(new GridCoordinate(15, 0)));

        assertTrue(gridBounds.isWithinBounds(new GridCoordinate(7, 12)));
        assertTrue(gridBounds.isWithinBounds(new GridCoordinate(14, 1)));
    }
}