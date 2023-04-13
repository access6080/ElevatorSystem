public class Floor {
    private final Lamp directionLamp;
    private final Lamp floorLamp;

    public int getFloorNum() {
        return floorNum;
    }

    private final int floorNum;

    public Floor(int floorNum) {
        this.directionLamp= new Lamp();
        this.floorLamp= new Lamp();
        this.floorNum=floorNum;
    }

    public void toggleDirectionLamp(){
        if (directionLamp.isLampState()){
            directionLamp.turnOn();
        }else {
            directionLamp.turnOff();
        }
    }

    public void toggleFloorLamp(){
        if (floorLamp.isLampState()){
            floorLamp.turnOn();
        }else {
            floorLamp.turnOff();
        }
    }
    public Boolean getFloorLampState(){
        return floorLamp.isLampState();
    }
    public Boolean getDirectionLampState(){
        return directionLamp.isLampState();
    }
}
