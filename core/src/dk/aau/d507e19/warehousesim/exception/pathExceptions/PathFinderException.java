package dk.aau.d507e19.warehousesim.exception.pathExceptions;

public class PathFinderException extends RuntimeException{
    public PathFinderException() {
    }

    public PathFinderException(String message) {
        super(message);
    }
}
