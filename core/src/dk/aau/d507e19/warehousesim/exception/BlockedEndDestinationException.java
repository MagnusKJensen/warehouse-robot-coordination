package dk.aau.d507e19.warehousesim.exception;

public class BlockedEndDestinationException extends PathFinderException {
    public BlockedEndDestinationException(String message) {
        System.err.println(message);
    }
}
