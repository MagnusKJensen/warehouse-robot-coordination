package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    public void isSameTrueTest() {
        GridCoordinate gridCoordinate = new GridCoordinate(5, 26);
        Position position = new Position(5f, 26f);

        assertTrue(position.isSameAs(gridCoordinate));
    }

    @Test
    public void isSameFalseTest() {
        GridCoordinate gridCoordinate = new GridCoordinate(5, 26);
        Position position = new Position(5f, 26.1f);

        assertFalse(position.isSameAs(gridCoordinate));
    }

    @Test
    public void equalsExactTrueTest(){
        Position position1 = new Position(5,65.9999f);
        Position position2 = new Position(5,65.9999f);
        assertEquals(position1, position2);
    }

    @Test
    public void equalsInexactTrueTest(){
        Position position1 = new Position(5,65.99999999f);
        Position position2 = new Position(5,65.99999998f);
        assertEquals(position1, position2);
    }

    @Test
    public void equalsSameObjectTrueTest(){
        Position position1 = new Position(5,65.99999999f);
        assertEquals(position1, position1);
    }

    @Test
    public void equalsFalseTest(){
        Position position1 = new Position(5,65.8f);
        Position position2 = new Position(5,65.7f);
        assertEquals(position1, position1);
    }

}