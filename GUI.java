import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;


public class GUI {
    private JLabel[][] elevInfo;
    private JPanel displayPanel;
    private int floorNum;
    private int elevNum;

    private static final int DEFAULT_FLOOR_NUM = 22;
    private static final int DEFAULT_ELEVATOR_NUM = 4;
    private static final int DEFAULT_COLUMN_WIDTH = 40;
    private static final int GUI_WIDTH = 160;
    private static final String[] options = {"Use Default Values", "Use User Inputs"};

    public enum Status{
        Open, Closed, Stuck
    }

    /**
     * Create the interface
     */
    public GUI() {
        int popUp = JOptionPane.showOptionDialog(null, "Which values would you like to use?", "Confirmation", JOptionPane.INFORMATION_MESSAGE, 0, null, options, options[0]);
        switch (popUp) {
            case -1 -> System.exit(0);
            case 0 -> {
                this.floorNum = DEFAULT_FLOOR_NUM;
                this.elevNum = DEFAULT_ELEVATOR_NUM;
            }
            case 1 -> {
                this.elevNum = Integer.parseInt(JOptionPane.showInputDialog("How many elevators?"));
                this.floorNum = Integer.parseInt(JOptionPane.showInputDialog("How many floors?"));
            }
        }

        initializeGUI();
    }

    /**
     * Initialize the frame to display the elevator's information
     */
    private void initializeGUI() {

        int heightOfRows = 10 * floorNum;
        int widthOfGUI = GUI_WIDTH;

        JFrame frmElevators = new JFrame();
        frmElevators.setTitle("Elevators");
        frmElevators.setBounds(100, 100, widthOfGUI, GUI_WIDTH + heightOfRows);
        frmElevators.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {widthOfGUI};
        gridBagLayout.rowHeights = new int[] {heightOfRows};
        gridBagLayout.columnWeights = new double[]{1.0};
        gridBagLayout.rowWeights = new double[]{0.0};
        frmElevators.getContentPane().setLayout(gridBagLayout);

        displayPanel = new JPanel();
        GridBagConstraints gbc_displayPanel = new GridBagConstraints();
        gbc_displayPanel.insets = new Insets(0, 0, 5, 0);
        gbc_displayPanel.anchor = GridBagConstraints.WEST;
        gbc_displayPanel.fill = GridBagConstraints.VERTICAL;
        gbc_displayPanel.gridx = 8;
        gbc_displayPanel.gridy = 0;
        frmElevators.getContentPane().add(displayPanel, gbc_displayPanel);
        GridBagLayout gbl_displayPanel = new GridBagLayout();
        int columns = 1 + elevNum + 1; // adds the floor column, elevator columns, then the data column
        int[] columnWidthds = new int[columns];
        for (int i = 0; i < columns; i++) {
            if(i != 1 + elevNum) {
                columnWidthds[i] = DEFAULT_COLUMN_WIDTH;
            }else {
                if (elevNum % 2 == 0) {//add width for elevator data
                    columnWidthds[i] = (elevNum / 2) * 150;
                }else {
                    columnWidthds[i] = ((elevNum / 2) + 1) * 150;
                }
            }
        }
        gbl_displayPanel.columnWidths = columnWidthds;
        gbl_displayPanel.rowHeights = new int[] {heightOfRows};
        double[] gblColumnWeights = new double[columns];
        for (int i = 0; i < columns; i++) {
            gblColumnWeights[i] = 0.0;
        }
        gbl_displayPanel.columnWeights = gblColumnWeights;
        gbl_displayPanel.rowWeights = new double[]{1.0};
        displayPanel.setLayout(gbl_displayPanel);

        elevatorInfoDisplay();

        frmElevators.setVisible(true);
        frmElevators.setResizable(true);
        frmElevators.setSize(400,300);
        frmElevators.setLocation(150, 100);
    }

    public void elevatorInfoDisplay(){
        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 0, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 3;
        gbc_panel.gridy = 0;
        displayPanel.add(panel, gbc_panel);

        int x;
        if (elevNum % 2 == 0) {//create grid layout if even or odd
            x = elevNum / 2;
        }else {
            x = (elevNum / 2) + 1;
        }

        panel.setLayout(new GridLayout(2, x, 0, 0));

        JPanel[] elevInfoPanels = new JPanel[elevNum];
        elevInfo = new JLabel[elevNum][4];
        for(int i = 0; i < elevNum; i++) {
            elevInfoPanels[i] = new JPanel();
            elevInfoPanels[i].setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)),"Elevator "+ i +" Info", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            panel.add(elevInfoPanels[i]);
            elevInfoPanels[i].setLayout(new GridLayout(0, 1, 0, 0));

            elevInfo[i][0] = new JLabel("Current Floor: 1");
            elevInfo[i][0].setFont(new Font("Ariel", Font.PLAIN, 14));
            elevInfoPanels[i].add(elevInfo[i][0]);

            elevInfo[i][1] = new JLabel("Direction: IDLE");
            elevInfo[i][1].setFont(new Font("Ariel", Font.PLAIN, 14));
            elevInfoPanels[i].add(elevInfo[i][1]);

            elevInfo[i][2] = new JLabel("Requests: STANDING BY");
            elevInfo[i][2].setFont(new Font("Ariel", Font.PLAIN, 14));
            elevInfoPanels[i].add(elevInfo[i][2]);

            elevInfo[i][3] = new JLabel("Doors: CLOSED");
            elevInfo[i][3].setFont(new Font("Ariel", Font.PLAIN, 14));
            elevInfoPanels[i].add(elevInfo[i][3]);
        }
    }

    public void setDoorsInfo(int elevatorNum, Status status) {
        if(status == Status.Open) {
            elevInfo[elevatorNum][3].setText("Doors: OPEN");
        }
        else if(status == Status.Stuck) {
            elevInfo[elevatorNum][3].setText("Doors: STUCK");
        }
        else if(status == Status.Closed) {
            elevInfo[elevatorNum][3].setText("Doors: CLOSED");
        }
    }

    public void setDirectionInfo(int elevatorNum, String direction) {
        elevInfo[elevatorNum][1].setText("Direction: " + direction);
    }

    public void setCurrentFloor(int elevatorNum, int currentFloor){
        elevInfo[elevatorNum][0].setText("Current Floor: " + currentFloor);
    }

    public void setRequest(int elevatorNum, String request){
        elevInfo[elevatorNum][2].setText("Requests: " + request);
    }

    public static void main(String[] args) {
        new GUI();
    }
}