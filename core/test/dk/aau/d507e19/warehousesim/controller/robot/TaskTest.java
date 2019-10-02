package dk.aau.d507e19.warehousesim.controller.robot;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void removeAllButCornersPathTest() {
        ArrayList<GridCoordinate> allCoordinates = new ArrayList<>();
        ArrayList<GridCoordinate> expectedStrippedCoordinates = new ArrayList<>();

        // Full path
        Collections.addAll(allCoordinates, new GridCoordinate(1, 5),
                new GridCoordinate(1, 6),
                new GridCoordinate(1, 7),
                new GridCoordinate(2, 7),
                new GridCoordinate(2, 8),
                new GridCoordinate(3, 7),
                new GridCoordinate(4, 7),
                new GridCoordinate(5, 7));

        // Only the corners
        Collections.addAll(expectedStrippedCoordinates, new GridCoordinate(1, 5),
                new GridCoordinate(1, 7),
                new GridCoordinate(2, 7),
                new GridCoordinate(2, 8),
                new GridCoordinate(3, 7),
                new GridCoordinate(5, 7));

        Task task = new Task(allCoordinates, Action.NONE);
        //assertEquals(expectedStrippedCoordinates, task.getStrippedPath());
    }

    @Test
    public void getPath() {
    }

    @Test
    public void getAction() {
    }

    @Test
    public void getTarget() {
    }

    @Test
    public void isValidPath() {
    }
}