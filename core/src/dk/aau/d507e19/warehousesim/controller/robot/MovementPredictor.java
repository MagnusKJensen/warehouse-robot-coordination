package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.plan.SpeedCalculator;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;

import java.util.ArrayList;

public class MovementPredictor {

    public static ArrayList<Reservation> calculateReservations
            (Robot robot, Path path, long startTimeTicks, long paddingTimeTicks) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        ArrayList<Line> lines = path.getLines();

        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            if(i > 0)
                startTimeTicks = reservations.get(reservations.size() - 1).getTimeFrame().getEnd() - paddingTimeTicks;
            reservations.addAll(calculateReservations(robot, line, startTimeTicks, paddingTimeTicks));
        }

        return combineChainedReservations(reservations);
    }

    public static long timeToTraverse(Robot robot, Path path){
        ArrayList<Reservation> reservations = calculateReservations(robot, path, 0, 0);
        return reservations.get(reservations.size() - 1).getTimeFrame().getEnd();
    }

    public static ArrayList<Reservation> calculateReservations(Robot robot, Line line, long startTimeTicks, long paddingTicks) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        ArrayList<GridCoordinate> coordinates = line.toCoordinates();
        SpeedCalculator lineSpeedCalculator = new SpeedCalculator(robot, line);

        int leaveDistance = coordinates.get(1).distanceFrom(coordinates.get(0));
        long leaveTime = lineSpeedCalculator.amountOfTicksToReach(leaveDistance);
        reservations.add(new Reservation(robot, line.getStart(), new TimeFrame(startTimeTicks - paddingTicks, startTimeTicks + leaveTime + paddingTicks)));

        GridCoordinate startCoordinate = coordinates.get(0);
        for(int i = 1; i < coordinates.size(); i++){
            GridCoordinate gridCoordinate = coordinates.get(i);
            int robotEnterDistance = gridCoordinate.distanceFrom(startCoordinate) - robot.getSize();
            int robotLeaveDistance = gridCoordinate.distanceFrom(startCoordinate) + robot.getSize();

            long timeToEnter = lineSpeedCalculator.amountOfTicksToReach(robotEnterDistance);
            long timeToLeave = lineSpeedCalculator.amountOfTicksToReach(robotLeaveDistance);

            TimeFrame timeFrame = new TimeFrame(startTimeTicks + timeToEnter - paddingTicks,
                    startTimeTicks + timeToLeave + paddingTicks);
            reservations.add(new Reservation(robot, gridCoordinate, timeFrame));
        }

        return reservations;
    }

    public static ArrayList<Reservation> combineChainedReservations(ArrayList<Reservation> originalReservations){
          ArrayList<Reservation> combinedReservations = new ArrayList<>();

          Reservation previousReservation = originalReservations.get(0);
          combinedReservations.add(previousReservation);

          for(int i = 1; i < originalReservations.size(); i ++){
              Reservation currentReservation = originalReservations.get(i);
              if(currentReservation.getGridCoordinate().equals(previousReservation.getGridCoordinate())){
                  // If the two reservations reserve the same coordinate, then combine them
                  combinedReservations.remove(combinedReservations.size() - 1);
                  currentReservation = combineSameTileReservations(previousReservation, currentReservation);
              }
              combinedReservations.add(currentReservation);
              previousReservation = currentReservation;
          }
        return combinedReservations;
    }

    private static Reservation combineSameTileReservations(Reservation res1, Reservation res2){
        if(!res1.getGridCoordinate().equals(res2.getGridCoordinate()))
            throw new IllegalArgumentException("Cannot combine two reservations on different coordinates :" +
                    "The given reservations had coordinates : " +
                    res1.getGridCoordinate() + " | "  + res2.getGridCoordinate());

        if(res1.getTimeFrame().getStart() > res2.getTimeFrame().getStart())
            throw new IllegalArgumentException("The first TimeFrame starts after the seconds time frame : \n TimeFrame 1: " +
                    res1.getTimeFrame() + " | TimeFrame 2: "  + res2.getTimeFrame());

        TimeFrame newTimeFrame = new TimeFrame(res1.getTimeFrame().getStart(), res2.getTimeFrame().getEnd());
        return new Reservation(res1.getRobot(), res1.getGridCoordinate(), newTimeFrame);
    }


}
