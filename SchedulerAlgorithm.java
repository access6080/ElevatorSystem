import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SchedulerAlgorithm {

    public int getBestCarToServiceJob(HashMap<Integer, Boolean> status, HashMap<Integer, Integer> locations,
                                       ElevatorEvent job){
        ArrayList<Integer> idleElevators = new ArrayList<>();
        ArrayList<Integer> nonIdleElevators = new ArrayList<>();

        // First, separate elevators to idle and not idle
        for (int i = 1; i < ElevatorSubsystem.ElevatorNumber + 1; i++) {
            if(status.get(i)){
                idleElevators.add(i);
            } else {
                nonIdleElevators.add(i);
            }
        }

        // Check if a non-idle elevator can service the request
        if(!nonIdleElevators.isEmpty()){
            for (int elevator : nonIdleElevators){
                if(job.floorButton().equals(FloorSubSystem.FloorButton.UP)
                        && locations.get(elevator) <= job.carButton()) return elevator;
                else if (job.floorButton().equals(FloorSubSystem.FloorButton.DOWN)
                        && locations.get(elevator) >= job.carButton()) return elevator;
            }
        }

        return getClosestElevator(idleElevators, locations, job.carButton());
    }


    /**
     * This method returns the  closest elevator to service  a request
     * @param idleElevators  A list of idle elevators
     * @param locations A mapping  of  elevators and thier current locations
     * @param reqFloor  The  floor being requested
     * @return the closest elevator
     */
    private int getClosestElevator(ArrayList<Integer> idleElevators, HashMap<Integer, Integer> locations, int reqFloor) {
        ArrayList<Integer> distances = new ArrayList<>();

        for (int  elevator: idleElevators) {
            int distance = reqFloor - locations.get(elevator);
            distances.add(Math.abs(distance));
        }

        int closet = Collections.min(distances);

        for (int el: idleElevators){
            int distance = reqFloor - locations.get(el);
            if(distance == closet) return el;
        }

        return idleElevators.get(0);
    }
}
