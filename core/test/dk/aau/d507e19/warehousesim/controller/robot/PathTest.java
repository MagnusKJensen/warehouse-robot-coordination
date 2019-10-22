package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class PathTest {

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


    @Test (expected = IllegalArgumentException.class)
    public void noncontinuousStraightPathTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        allCoordinates.add(new Step(2, 1));
        allCoordinates.add(new Step(4, 1));
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
    }

    @Test (expected = IllegalArgumentException.class)
    public void repeatedStepInvalidPathTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        allCoordinates.add(new Step(2, 1));
        allCoordinates.add(new Step(2, 1));
        allCoordinates.add(new Step(3, 1));
        allCoordinates.add(new Step(4, 1));
        Path path = new Path(allCoordinates);
    }

    @Test (expected = IllegalArgumentException.class)
    public void repeatedWaitStepInvalidPathTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        allCoordinates.add(new Step(2, 1));
        allCoordinates.add(new Step(2, 1, 50));
        allCoordinates.add(new Step(2, 1, 50));
        allCoordinates.add(new Step(3, 1));
        allCoordinates.add(new Step(4, 1));
        Path path = new Path(allCoordinates);
    }

    @Test (expected = IllegalArgumentException.class)
    public void waitStepWrongPosition() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        // Add a waiting step without a step to reach the coordinate first
        allCoordinates.add(new Step(2, 1, 50));
        allCoordinates.add(new Step(3, 1));
        allCoordinates.add(new Step(4, 1));
        Path path = new Path(allCoordinates);
    }

    @Test (expected = IllegalArgumentException.class)
    public void missingCornerInvalidPath() {
        ArrayList<Step> allCoordinates = new ArrayList<>();
        allCoordinates.add(new Step(1, 1));
        allCoordinates.add(new Step(2, 1));
        allCoordinates.add(new Step(3, 2));
        allCoordinates.add(new Step(3, 3));
        Path path = new Path(allCoordinates);
    }

    @Test
    public void validPathWithPauseTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();

        allCoordinates.add(new Step(2, 2));
        allCoordinates.add(new Step(3, 2));
        allCoordinates.add(new Step(3, 2, 50));
        allCoordinates.add(new Step(4, 2));

        Path path = new Path(allCoordinates);
    }

    @Test
    public void getLineWithPauseTest() {
        ArrayList<Step> allCoordinates = new ArrayList<>();

        allCoordinates.add(new Step(2, 2));
        allCoordinates.add(new Step(3, 2));
        allCoordinates.add(new Step(3, 2, 50));
        allCoordinates.add(new Step(4, 2));

        Path path = new Path(allCoordinates);

        ArrayList<Line> expectedLines = new ArrayList<>();
        expectedLines.add(new Line(new Step(2, 2), new Step(3, 2, 50)));
        expectedLines.add(new Line(new Step(3, 2, 50), new Step(4, 2)));

        assertEquals(expectedLines, path.getLines());
    }

    @Test
    public void getStrippedPath() {
        ArrayList<Step> allCoordinates = new ArrayList<>();

        allCoordinates.add(new Step(2, 2));
        allCoordinates.add(new Step(3, 2));
        allCoordinates.add(new Step(3, 2, 50));
        allCoordinates.add(new Step(4, 2));
        allCoordinates.add(new Step(4, 3));
        allCoordinates.add(new Step(5, 3));
        allCoordinates.add(new Step(6, 3));

        ArrayList<Step> expectedStrippedPath = new ArrayList<>();
        expectedStrippedPath.add(new Step(2, 2));
        expectedStrippedPath.add(new Step(3, 2, 50));
        expectedStrippedPath.add(new Step(4, 2));
        expectedStrippedPath.add(new Step(4, 3));
        expectedStrippedPath.add(new Step(6, 3));

        Path path = new Path(allCoordinates);
        assertEquals(expectedStrippedPath, path.getStrippedPath());
    }
}