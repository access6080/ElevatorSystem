import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ElevatorPanel extends JPanel {
    private JPanel idleStatePanel;
    private JPanel currentFloorPanel;
    private JButton idleState;
    private JButton currentFloor;
    public enum elevatorIdleState{IDLE,NOT_IDLE,BROKEN}


    public ElevatorPanel(int number) {
        super(new BorderLayout());
        this.add(new JLabel("Elevator " + number), BorderLayout.LINE_START);

        this.idleStatePanel = new JPanel();
        idleStatePanel.setLayout(new BoxLayout(idleStatePanel, BoxLayout.Y_AXIS));
        idleStatePanel.setBorder(new EmptyBorder(0,50,0,0));
        idleStatePanel.add(new JLabel("Idle Status"));

        this.idleState = new JButton();
        idleState.setBackground(Color.yellow);
        idleState.setOpaque(true);
        idleState.setBorderPainted(true);
        idleStatePanel.add(idleState);
        this.add(idleStatePanel, BorderLayout.CENTER);


        this.currentFloorPanel = new JPanel();
        currentFloorPanel.setLayout(new BoxLayout(currentFloorPanel, BoxLayout.Y_AXIS));
        currentFloorPanel.setBorder(new EmptyBorder(0,0,0,130));
        currentFloorPanel.add(new JLabel("Current floor"));


        currentFloor = new JButton("1");
        currentFloorPanel.add(currentFloor);
        this.add(currentFloorPanel, BorderLayout.EAST);
        this.setBorder(new EmptyBorder(5, 20,5,10));
   }

    public void changeIdleStatus(elevatorIdleState state){
        switch(state){
            case IDLE: setBackground(Color.YELLOW);
            case NOT_IDLE:setBackground(Color.GREEN);
            case BROKEN:setBackground(Color.RED);
        }
    }

    public void updateCurrentFloor(int currentFloor){
        this.currentFloor.setText(String.valueOf(currentFloor));
        this.repaint();
        this.revalidate();
    }

}
