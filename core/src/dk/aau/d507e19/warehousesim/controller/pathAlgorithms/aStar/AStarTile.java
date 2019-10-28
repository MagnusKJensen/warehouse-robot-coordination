package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

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
}