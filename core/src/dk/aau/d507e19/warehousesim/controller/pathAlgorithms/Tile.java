package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

public class Tile {
    private int currentXPosition;
    private int currentYPosition;
    private int previousXposition;
    private int getPreviousYposition;
    private int H;
    private int G = 0;
    private int F;
    private boolean isBlocked = false;

    public Tile(int currentXPosition, int currentYPosition) {
        this.currentXPosition = currentXPosition;
        this.currentYPosition = currentYPosition;
    }

    public void setPreviousXposition(int previousXposition) {
        this.previousXposition = previousXposition;
    }

    public void setPreviousYposition(int getPreviousYposition) {
        this.getPreviousYposition = getPreviousYposition;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public int getCurrentXPosition() {
        return currentXPosition;
    }

    public int getCurrentYPosition() {
        return currentYPosition;
    }

    public int getPreviousXposition() {
        return previousXposition;
    }

    public int getGetPreviousYposition() {
        return getPreviousYposition;
    }

    public boolean isBlocked() {
        return isBlocked;
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

    public void calculateH(int xFinalPosition, int yFinalPosition) {

        H = Math.abs((xFinalPosition - currentXPosition)) + Math.abs((yFinalPosition- currentYPosition));

    }
    public int calculateG(int previousG) {
        G =  previousG + 1;
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
                ", previousXposition=" + previousXposition +
                ", getPreviousYposition=" + getPreviousYposition +
                ", H=" + H +
                ", G=" + G +
                ", F=" + F +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
