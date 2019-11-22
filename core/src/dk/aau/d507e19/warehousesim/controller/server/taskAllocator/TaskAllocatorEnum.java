package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

public enum TaskAllocatorEnum {
    DUMMY_TASK_ALLOCATOR("DummyTaskAllocator", true), NAIVE_SHORTEST_DISTANCE_TASK_ALLOCATOR("NaiveShortestDistanceTaskAllocator", true), LEAST_USED_ROBOT_TASK_ALLOCATOR("LeastUsedRobotTaskAllocator", true), SMART_ALLOCATOR("\"Smart\"Allocator", true);

    private String name;
    private boolean works;
    TaskAllocatorEnum(String name, boolean works) {
        this.works = works;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean works() {
        return works;
    }

    public TaskAllocator getTaskAllocator(StorageGrid grid, Server server){
        switch (this) {
            case DUMMY_TASK_ALLOCATOR:
                return new DummyTaskAllocator(server);
            case NAIVE_SHORTEST_DISTANCE_TASK_ALLOCATOR:
                return new NaiveShortestDistanceTaskAllocator(grid, server);
            case LEAST_USED_ROBOT_TASK_ALLOCATOR:
                return new LeastUsedRobotTaskAllocator(grid, server);
            case SMART_ALLOCATOR:
                return new SmartAllocator(server);
            default:
                throw new RuntimeException("Could not identify task allocator " + this.getName());
        }
    }
}
