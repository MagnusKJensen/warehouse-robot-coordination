package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TaskAllocator {

    protected ArrayList<BinDelivery> unassignedTasks = new ArrayList<>();
    protected ArrayList<BinDelivery> delegatedTasks = new ArrayList<>();

    public abstract void update();

    public void addTask(BinDelivery binDelivery){
        binDelivery.reset();
        unassignedTasks.add(binDelivery);
    }

    public void addTask(List<BinDelivery> binDeliveries){
        unassignedTasks.addAll(binDeliveries);
    }

    public Iterator<BinDelivery> getTaskIterator(){
        return unassignedTasks.iterator();
    }

}
