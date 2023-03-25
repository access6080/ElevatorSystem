/**this class deals with movement of the motor. It starts or stops the motor and logs if it has been started or stopped.
 * @author Chibuzo Okpara
 * @version March 10th, 2023
 */
public class Motor {
    private boolean status;

    private final Logger logger;
    public Motor(){
        this.logger = new Logger();
        status = false;

    }

    /**this returns the current status of the motor
     * @return status returns the status of the motor.
     */
    public boolean getStatus(){return status;}

    /**
     *this method starts the motor
     */
    public void startMotor(){
        this.status= true;
        logger.info("Motor started");}

    /**
     *this method stops the motor
     */
    public void stopMotor(){
        this.status =false;
        logger.info("Motor stopped");
    }
}