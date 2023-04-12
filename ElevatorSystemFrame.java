import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author chibuzo Okpara
 */
public class ElevatorSystemFrame extends JFrame implements ElevatorSystemView {
    private int floorNum;
    private int elevNum;
    private JPanel floorSubSystemControlPanel;
    private JPanel schedulerControlPanel;
    private JPanel elevatorSubSystemControlPanel;
    private FloorPanel[] floorPanels;
    private ElevatorPanel[] elevatorPanels;
    private JPanel schedulerOutputPanel;

    public ElevatorSystemFrame() {
        String[] options = {"Default Values", "Custom values"};

        int popUp = JOptionPane.showOptionDialog(null, "Which values would you like to use?",
                "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        switch (popUp) {
            case -1 -> System.exit(0);
            case 0 -> {
                this.floorNum = UtilityInformation.NUMBER_OF_FLOORS;
                this.elevNum = UtilityInformation.NUMBER_OF_ELEVATORS;
            }
            case 1 -> {
                this.elevNum = Integer.parseInt(JOptionPane.showInputDialog("How many elevators?"));
                this.floorNum = Integer.parseInt(JOptionPane.showInputDialog("How many floors?"));
            }
        }

        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Elevator Control Panel");
        setPreferredSize(new Dimension(1000, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);


        this.floorSubSystemControlPanel = new JPanel(new BorderLayout());
        floorSubSystemControlPanel.setBorder( BorderFactory.createLineBorder(Color.black));
        floorSubSystemControlPanel.setPreferredSize(new Dimension(500, 800));
        this.add(floorSubSystemControlPanel, BorderLayout.LINE_START);
        floorSubSystemControlPanel.add(new JLabel("Floor Subsystem", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel floorList = new JPanel();
        floorList.setLayout(new BoxLayout(floorList, BoxLayout.Y_AXIS));

        floorPanels = new FloorPanel[floorNum + 1];
        for(int i = 1; i < (floorNum + 1); i++){
            FloorPanel p = new FloorPanel(i);
            floorPanels[i] = p;
            floorList.add(p);
        }

        floorSubSystemControlPanel.add(new JScrollPane(floorList), BorderLayout.CENTER);



        this.elevatorSubSystemControlPanel =  new JPanel(new BorderLayout());
        elevatorSubSystemControlPanel.setBorder(new LineBorder(Color.BLACK));
        elevatorSubSystemControlPanel.setPreferredSize(new Dimension(500, 350));
        elevatorSubSystemControlPanel.add(new JLabel("Elevator Subsystem", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel elevatorList = new JPanel();
        elevatorList.setLayout(new BoxLayout(elevatorList, BoxLayout.Y_AXIS));

        elevatorPanels = new ElevatorPanel[elevNum + 1];
        for(int i = 1; i < (elevNum + 1); i++){
            ElevatorPanel p = new ElevatorPanel(i);
            elevatorPanels[i] = p;
            elevatorList.add(p);
        }
        elevatorSubSystemControlPanel.add(new JScrollPane(elevatorList), BorderLayout.CENTER);

        this.schedulerControlPanel =  new JPanel(new BorderLayout());
        schedulerControlPanel.setBorder(new LineBorder(Color.BLACK));
        schedulerControlPanel.setPreferredSize(new Dimension(500, 400));
        schedulerControlPanel.add(new JLabel("Scheduler", SwingConstants.CENTER), BorderLayout.PAGE_START);

        schedulerOutputPanel = new JPanel();
        schedulerOutputPanel.setLayout(new BoxLayout(schedulerOutputPanel, BoxLayout.Y_AXIS));


        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new LineBorder(Color.BLACK));
        rightPanel.setPreferredSize(new Dimension(500, 800));
        rightPanel.add(elevatorSubSystemControlPanel, BorderLayout.NORTH);
        rightPanel.add(schedulerControlPanel, BorderLayout.SOUTH);


        this.add(rightPanel, BorderLayout.LINE_END);
        setVisible(true);
        this.pack();
    }

    public void addLog(String log){
        schedulerOutputPanel.add(new JLabel(log));
    }
    
    public static void main(String[] args) {
        new ElevatorSystemFrame();
    }

    @Override
    public void update() {}


}
