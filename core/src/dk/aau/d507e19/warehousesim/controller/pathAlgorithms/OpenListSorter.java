package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import java.util.Comparator;

public class OpenListSorter implements Comparator<Tile> {

    @Override
    public int compare(Tile o1, Tile o2) {
        return Integer.compare(o1.calculateF(), o2.calculateF());
    }
}
