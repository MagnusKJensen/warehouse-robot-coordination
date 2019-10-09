package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import java.util.Comparator;

public class OpenListSorter implements Comparator<AStarTile> {

    @Override
    public int compare(AStarTile o1, AStarTile o2) {
        return Integer.compare(o1.calculateF(), o2.calculateF());
    }
}
