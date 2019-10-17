package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class PathTest {

/*
    @Test
    public void removeAllButCornersPathTest() {
        ArrayList<Step> allSteps = new ArrayList<>();
        ArrayList<Step> expectedStrippedCoordinates = new ArrayList<>();

        // Full path
        Collections.addAll(allSteps,
                new Step(1, 1),
                new Step(1, 2),
                new Step(1, 3),
                new Step(2, 3),
                new Step(2, 4),
                new Step(3, 4),
                new Step(4, 4),
                new Step(4, 3),
                new Step(4, 2),
                new Step(4, 1));

        // Only the corners
        Collections.addAll(expectedStrippedCoordinates,
                new Step(1, 1),
                new Step(1, 3),
                new Step(2, 3),
                new Step(2, 4),
                new Step(4, 4),
                new Step(4, 1));

        Path path = new Path(allSteps);
        assertEquals(expectedStrippedCoordinates, path.getStrippedPath());
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeAllButCornersZeroLenTest() {
        ArrayList<GridCoordinate> allCoordinates = new ArrayList<>();
        Path path = new Path(Step.fromGridCoordinates(allCoordinates));
    }


    @Ignore
    public void removeAllButCornersOneLenTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(0,0));
        Path path = new Path(allCoordinates);
        assertTrue(path.getStrippedPath().isEmpty());
    }

    @Test (expected = IllegalArgumentException.class)
    public void noncontinuousStraightPathTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        allCoordinates.add(new Step(4, 1));
        allCoordinates.add(new Step(2, 1));
        Path path = new Path(allCoordinates);
    }

    @Test (expected = IllegalArgumentException.class)
    public void noncontinuousCornerPathTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        allCoordinates.add(new Step(2, 1));
        allCoordinates.add(new Step(3, 1));
        allCoordinates.add(new Step(3, 3));
        Path path = new Path(allCoordinates);
    }*/
}