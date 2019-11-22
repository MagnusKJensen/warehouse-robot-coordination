package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

import java.util.Objects;

public class AStarTile {
    private int currentXPosition;
    private int currentYPosition;
    private int previousXPosition;
    private int getPreviousYPosition;
    private int H;
    private int G = 0;
    private int F;
    private boolean isBlocked = false;


    public AStarTile(int currentXPosition, int currentYPosition) {
        this.currentXPosition = currentXPosition;
        this.currentYPosition = currentYPosition;
    }

    //Setters

    public void setPreviousXPosition(int previousXPosition) {
        this.previousXPosition = previousXPosition;
    }

    public void setPreviousYPosition(int getPreviousYPosition) {
        this.getPreviousYPosition = getPreviousYPosition;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void setG(int g) {
        G = g;
    }

    public void setH(int h) { H = h;}

    //Getters

    public int getCurrentXPosition() {
        return currentXPosition;
    }

    public int getCurrentYPosition() {
        return currentYPosition;
    }

    public int getPreviousXPosition() {
        return previousXPosition;
    }

    public int getGetPreviousYPosition() {
        return getPreviousYPosition;
    }

    public int getG() {
        return G;
    }

    public int getH() {
        return H;
    }

    public int getF() {
        return F;
    }

    //Methods

    public boolean isBlocked() {
        return isBlocked;
    }

    public void calculateH(int xFinalPosition, int yFinalPosition) {
        H = Math.abs((xFinalPosition - currentXPosition)) + Math.abs((yFinalPosition - currentYPosition));
    }

    public int calculateG(int previousG) {
        G = previousG + 1;
        return G;
    }

    public int calculateF() {
        F = G + H;
        return F;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "currentXPosition=" + currentXPosition +
                ", currentYPosition=" + currentYPosition +
                ", previousXPosition=" + previousXPosition +
                ", getPreviousYPosition=" + getPreviousYPosition +
                ", H=" + H +
                ", G=" + G +
                ", F=" + F +
                ", isBlocked=" + isBlocked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AStarTile aStarTile = (AStarTile) o;
        return currentXPosition == aStarTile.currentXPosition &&
                currentYPosition == aStarTile.currentYPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentXPosition, currentYPosition);
    }

    public AStarTile copy(){
        AStarTile ast = new AStarTile(this.getCurrentXPosition(), this.getCurrentYPosition());
        ast.setPreviousXPosition(this.getPreviousXPosition());
        ast.setPreviousYPosition(this.getPreviousYPosition);
        ast.setH(this.getH());
        ast.setG(this.getG());
        ast.setBlocked(this.isBlocked);

        return ast;
    }
}
