import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FloorPanel extends JPanel {
    private JButton floorLamp;
    private  JPanel directionLampPanel;
    private  JPanel floorLampPanel;

    private final JButton directionLamp;

    public FloorPanel(int number) {
        super(new BorderLayout());
        this.add(new JLabel("Floor " + number), BorderLayout.LINE_START);

        this.directionLampPanel = new JPanel();
        directionLampPanel.setLayout(new BoxLayout(directionLampPanel, BoxLayout.Y_AXIS));
        directionLampPanel.setBorder(new EmptyBorder(0,50,0,0));
        directionLampPanel.add(new JLabel("Direction Lamp"));

        this.directionLamp = new JButton();
        directionLamp.setBackground(Color.RED);
        directionLampPanel.add(directionLamp);
        this.add(directionLampPanel, BorderLayout.CENTER);

        this.floorLampPanel = new JPanel();
        floorLampPanel.setLayout(new BoxLayout(floorLampPanel, BoxLayout.Y_AXIS));
        floorLampPanel.setBorder(new EmptyBorder(0,0,0,130));
        floorLampPanel.add(new JLabel("Floor Lamp"));

        this.floorLamp = new JButton();
        floorLamp.setBackground(Color.yellow);
        floorLampPanel.add(floorLamp);
        this.add(floorLampPanel, BorderLayout.EAST);

        this.setBorder(new EmptyBorder(30,10,30,10));
    }

    public void toggleDirectionLamp(){
        if(directionLamp.getBackground() == Color.RED){
            directionLamp.setBackground(Color.GREEN);
        } else {
            directionLamp.setBackground(Color.RED);
        }
    }

    public void toggleFloorLamp(){
        if(floorLamp.getBackground() == Color.YELLOW){
            floorLamp.setBackground(Color.GREEN);
        } else {
            floorLamp.setBackground(Color.YELLOW);
        }
    }

}
