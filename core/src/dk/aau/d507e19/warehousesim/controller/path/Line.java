package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class Line {

    private Step start, end;

    public Line(Step start, Step end) {
        this.start = start;
        this.end = end;
    }

    public Step getStart() {
        return start;
    }

    public Step getEnd() {
        return end;
    }
}
