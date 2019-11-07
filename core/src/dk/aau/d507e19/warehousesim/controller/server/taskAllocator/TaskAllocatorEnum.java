package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

public enum TaskAllocatorEnum {
    DUMMY_TASK_ALLOCATOR("DummyTaskAllocator", true), NAIVE_SHORTEST_DISTANCE_TASK_ALLOCATOR("NaiveShortestDistanceTaskAllocator", true), LEAST_USED_ROBOT_TASK_ALLOCATOR("LeastUsedRobotTaskAllocator", true);

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

    public TaskAllocator getTaskAllocator(StorageGrid grid){
        switch (this) {
            case DUMMY_TASK_ALLOCATOR:
                return new DummyTaskAllocator();
            case NAIVE_SHORTEST_DISTANCE_TASK_ALLOCATOR:
                return new NaiveShortestDistanceTaskAllocator(grid);
            case LEAST_USED_ROBOT_TASK_ALLOCATOR:
                return new LeastUsedRobotTaskAllocator(grid);
            default:
                throw new RuntimeException("Could not identify task allocator " + this.getName());
        }
    }
}
