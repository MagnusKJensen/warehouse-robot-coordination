package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.path.Path;

import java.util.Optional;

public interface PathFinder {

    Optional<Path> calculatePath(GridCoordinate start, GridCoordinate destination);

}
