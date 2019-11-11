package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;


import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


public class AStarTileTest {
    AStarTile aStarTile;

    @Test
    public void setPreviousXPosition() {
        aStarTile = new AStarTile(1, 1);
        //Sets the previus x postion that was used by Astar to calculate path
        aStarTile.setPreviousXPosition(0);
        assertEquals(aStarTile.getPreviousXPosition(), 0);
    }

    @Test
    public void setPreviousYPosition() {
        aStarTile = new AStarTile(1, 1);
        //Sets the previus y postion that was used by Astar to calculate path
        aStarTile.setPreviousYPosition(0);

        assertEquals(aStarTile.getGetPreviousYPosition(), 0);
    }

    @Test
    public void setBlocked() {
        aStarTile = new AStarTile(1, 1);
        //blocks astar tile wich meeans it cant be accesed by astar
        aStarTile.setBlocked(true);
        assertEquals(aStarTile.isBlocked(), true);
    }

    @Test
    public void setG() {
        aStarTile = new AStarTile(1, 1);
        //sets g witch is the steps taken by astar
        aStarTile.setG(1);

        assertEquals(aStarTile.getG(), 1);
    }

    @Test
    public void getCurrentXPosition() {
        aStarTile = new AStarTile(1, 1);
        assertEquals(aStarTile.getCurrentXPosition(), 1);
    }

    @Test
    public void getCurrentYPosition() {
        aStarTile = new AStarTile(1, 1);
        assertEquals(aStarTile.getCurrentYPosition(), 1);
    }

    @Test
    public void getPreviousXPosition() {
        aStarTile = new AStarTile(1, 1);
        aStarTile.setPreviousXPosition(0);
        assertEquals(aStarTile.getPreviousXPosition(), 0);
    }

    @Test
    public void getGetPreviousYPosition() {
        aStarTile = new AStarTile(1, 1);
        aStarTile.setPreviousYPosition(0);
        assertEquals(aStarTile.getGetPreviousYPosition(), 0);
    }

    @Test
    public void getF() {
        aStarTile = new AStarTile(1, 1);
        aStarTile.calculateG(0);
        aStarTile.calculateH(1,2);
        // calculates F based on H and G
        aStarTile.calculateF();
        assertEquals(aStarTile.getF(), 2);
    }

    @Test
    public void isBlocked() {
        aStarTile = new AStarTile(1, 1);
        aStarTile.setBlocked(false);
        assertEquals(aStarTile.isBlocked(), false);
        aStarTile.setBlocked(true);
        assertEquals(aStarTile.isBlocked(), true);
    }

    @Test
    public void calculateH() {
        aStarTile = new AStarTile(1, 1);
        //calculates hueristic path to final destination
        aStarTile.calculateH(1,2);
        assertEquals(aStarTile.getH(), 1);
    }

    @Test
    public void calculateG() {
        aStarTile = new AStarTile(1, 1);
        //increments g with 1 to simulate a step taken
        aStarTile.calculateG(0);
        assertEquals(aStarTile.getG(), 1);
    }

    @Test
    public void calculateF() {
        aStarTile = new AStarTile(1, 1);

        aStarTile.calculateG(0);
        //calculates hueristic path to final destination
        aStarTile.calculateH(1,2);
        // calculates F based on H and G
        aStarTile.calculateF();
        assertEquals(aStarTile.getF(), 2);
    }
}
