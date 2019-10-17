package dk.aau.d507e19.warehousesim.controller.robot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GridCoordinateTest {

    @Test
    public void isStepValidContinuationOfTrue() {
        GridCoordinate centerCoord = new GridCoordinate(1,1);
        assertTrue(centerCoord.isNeighbourOf(new GridCoordinate(1, 2)));
        assertTrue(centerCoord.isNeighbourOf(new GridCoordinate(1, 0)));
        assertTrue(centerCoord.isNeighbourOf(new GridCoordinate(2, 1)));
        assertTrue(centerCoord.isNeighbourOf(new GridCoordinate(0, 1)));
    }

    @Test
    public void isStepValidContinuationOfDiagonal() {
        GridCoordinate centerCoord = new GridCoordinate(7,8);
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(8, 9)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(6, 7)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(8, 6)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(6, 6)));
    }

    @Test
    public void isStepValidContinuationOfFarAway() {
        GridCoordinate centerCoord = new GridCoordinate(1,1);
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(1, 3)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(1, -1)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(3, 1)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(-1, 1)));
        assertFalse(centerCoord.isNeighbourOf(new GridCoordinate(5, 5)));
    }

}