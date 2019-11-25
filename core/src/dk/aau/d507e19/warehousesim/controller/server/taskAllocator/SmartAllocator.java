package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SmartAllocator extends TaskAllocator{

    private Server server;
    private HashMap<PickerTile, Integer> pickerTileBusyValue = new HashMap<>();

    public SmartAllocator(Server server) {
        this.server = server;
        initPickerMap();
    }

    private void initPickerMap() {
        ArrayList<PickerTile> pickerTiles = server.getPickerTiles();
        for(PickerTile picker : pickerTiles)
            pickerTileBusyValue.put(picker, 0);
    }

    private void incrementTaskCount(PickerTile pickerTile){
        pickerTileBusyValue.put(pickerTile, pickerTileBusyValue.get(pickerTile) + 1);
    }

    private void decrementTaskCount(PickerTile pickerTile){
        pickerTileBusyValue.put(pickerTile, pickerTileBusyValue.get(pickerTile) - 1);
    }

    @Override
    public void update() {
        ArrayList<Robot> availableRobots = new ArrayList<>(server.getAvailableRobots());

        while(availableRobots.size() > 0 && unassignedTasks.size() > 0){
            BinDelivery leastCrowdedTask = getLeastCrowded(unassignedTasks);
            Robot closestRobot = getClosestRobot(availableRobots, leastCrowdedTask);

            // Assign task
            availableRobots.remove(closestRobot);
            unassignedTasks.remove(leastCrowdedTask);

            // Increment busy counter when started and decrement when finished
            incrementTaskCount(leastCrowdedTask.getOrder().getPicker());
            leastCrowdedTask.addOnCompleteAction(() -> decrementTaskCount(leastCrowdedTask.getOrder().getPicker()));

            assignTask(leastCrowdedTask, closestRobot);
        }
    }

    private Robot getClosestRobot(ArrayList<Robot> availableRobots, BinDelivery binDelivery) {
        Robot bestCandidate = availableRobots.get(0);
        int shortestDistance = Integer.MAX_VALUE;
        for(Robot r : availableRobots){
            int distance = r.getGridCoordinate().manhattanDistanceFrom(binDelivery.getBinCoords());
            if(distance < shortestDistance){
                if(distance == 0) return r;
                shortestDistance = distance;
                bestCandidate = r;
            }
        }

        return bestCandidate;
    }

    private BinDelivery getLeastCrowded(ArrayList<BinDelivery> unassignedTasks) {
        BinDelivery leastCrowded = unassignedTasks.get(0);
        int minCrowded = Integer.MAX_VALUE;

        for(BinDelivery binDelivery : unassignedTasks){
            int crowdLevel = pickerTileBusyValue.get(binDelivery.getOrder().getPicker());
            if(crowdLevel < minCrowded){
                if(crowdLevel == 0) return binDelivery;
                minCrowded = crowdLevel;
                leastCrowded = binDelivery;
            }
        }

        return leastCrowded;
    }

}
