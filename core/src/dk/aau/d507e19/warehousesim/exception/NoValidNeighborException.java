package dk.aau.d507e19.warehousesim.exception;

public class NoValidNeighborException extends PathFinderException {

    public NoValidNeighborException(String message) {
        System.err.println(message);
    }
}
