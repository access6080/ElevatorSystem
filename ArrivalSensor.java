public class ArrivalSensor {

    private final int floorNum;
    private boolean isActive;

    public ArrivalSensor(int floorNum) {
        this.floorNum = floorNum;
        this.isActive =  false;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private void notifyScheduler()  {

    }
}
