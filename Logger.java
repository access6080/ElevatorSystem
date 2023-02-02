import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * This class which handles the logging of the ElevatorSystem.
 *
 * @author Oluwatomisin Ajayi
 * @author Boma Iyaye
 * @version January 31st, 2023
 */
public class Logger {
    private final ArrayList<String> logs;

    public Logger(){
        logs = new ArrayList<>();
    }

    /**
     *  This method logs an error whenever it occurs in the ElevatorSystem.
     * @param message
     */
    public void error(String message){
        StringBuilder log = formatLog(" ERROR: ", message);

        logs.add(log.toString());
        System.out.println(log);
    }

    /**
     * This method logs information of what happens in the ElevatorSystem.
     * @param message
     */
    public void info(String message){
        StringBuilder log = formatLog(" INFO: ", message);

        logs.add(log.toString());
        System.out.println(log);
    }

    /**
     * This method returns the format of the logger file.
     * @param level
     * @param message
     * @return
     */
    private static StringBuilder formatLog(String level, String message) {
        StringBuilder log = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        log.append("[").append(Thread.currentThread().getName()).append("] ")
                .append(dtf.format(now)).append(level).append(message);
        return log;
    }

    /**
     * This method saves the information in the logger to a file.
     * @throws IOException
     */
    public void save() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String filename = "[" + Thread.currentThread().getName() + "] " + dtf.format(now) +".txt";

        File file = new File(filename);
        System.out.println(filename);
        if(!file.createNewFile()) System.out.println(formatLog("ERROR:", "Error Creating file"));

        try(PrintWriter writer = new PrintWriter(file)){
            for (String log : logs){
                writer.println(log);
            }

        } catch (FileNotFoundException e){
            throw new FileNotFoundException("Error Creating File");
        }
    }
    
}
