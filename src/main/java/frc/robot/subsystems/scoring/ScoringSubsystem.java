package frc.robot.subsystems.scoring;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ScoringSubsystem extends SubsystemBase {

    // Constants for motor and limits
    // All can be changable
    private static final int ELEVATOR_MOTOR_PWM_CHANNEL = 3; // PWM channel for the elevator motor
    private static final int LAUNCHER_MOTOR_PWM_CHANNEL = 4; // PWM channel for the launcher motor
    private static final int ENCODER_CHANNEL_A = 0; // Encoder channel A
    private static final int ENCODER_CHANNEL_B = 1; // Encoder channel B

    // Placeholder values for max, min, and step heights. CHANGE LATER.
    public static final double MAX_ELEVATOR_HEIGHT = 100.0; // Max encoder units (example)
    public static final double MIN_ELEVATOR_HEIGHT = 0.0;   // Min encoder units (example)
    public static final double STEP_1 = 25;
    public static final double STEP_2 = 50;
    public static final double STEP_3 = 75;
    public static final double STEP_4 = 100;

    private static final double JOYSTICK_DEADZONE = 0.1;     // Deadzone for joystick input
    private static final double ELEVATOR_SPEED = 0.5;        // Base speed for manual control

    public static double globalTargetRotations = 0; //static variable storing target rotations for the elevator.

    // Motor, encoder, and joystick instances
    private final PWMSparkMax elevatorMotor = new PWMSparkMax(ELEVATOR_MOTOR_PWM_CHANNEL);
    private final PWMSparkMax launcherMotor = new PWMSparkMax(LAUNCHER_MOTOR_PWM_CHANNEL);
    private final Encoder elevatorEncoder = new Encoder(ENCODER_CHANNEL_A, ENCODER_CHANNEL_B);
    private final Joystick joystick = new Joystick(1); // Joystick port
    // Update the joystick port number if your joystick is connected to a different port

    public ScoringSubsystem() {
        // Encoder setup: distance per pulse, reverse direction if needed
        elevatorEncoder.setDistancePerPulse(1.0); // Set appropriately for your encoder
        // Change the distance per pulse to match the specific configuration of your encoder (e.g., steps per revolution)
        elevatorEncoder.reset();
    }

    @Override
    public void periodic() {
        // Read joystick input
        double joystickValue = -joystick.getY(); // Negative for forward control

        // Apply deadzone to joystick input
        if (Math.abs(joystickValue) < JOYSTICK_DEADZONE) {
            joystickValue = 0;
        }

        // Calculate target position based on joystick input
        double currentHeight = elevatorEncoder.getDistance();
        double targetHeight = 0; //PLACEHOLDER: MAKE A STATIC VARIABLE IN ROBOTCONTAINER TO STORE TARGET HEIGHT

        // Clamp target height to within safe limits
        if (targetHeight > MAX_ELEVATOR_HEIGHT) {
            targetHeight = MAX_ELEVATOR_HEIGHT;
        } else if (targetHeight < MIN_ELEVATOR_HEIGHT) {
            targetHeight = MIN_ELEVATOR_HEIGHT;
        }

        // Control motor to move toward target height
        if (joystickValue != 0) {
            if (targetHeight > currentHeight) {
                elevatorMotor.set(ELEVATOR_SPEED); // Move up
            } else if (targetHeight < currentHeight) {
                elevatorMotor.set(-ELEVATOR_SPEED); // Move down
            }
        } else {
            elevatorMotor.set(0); // Stop motor
        }
    }

    public void moveBasic() //Tells the elevator to move to the current target, without changing the target.
    {
        double currentHeight = elevatorEncoder.getDistance();

        if(ScoringSubsystem.globalTargetRotations > currentHeight)
        {
            elevatorMotor.set(ELEVATOR_SPEED);
        }
        else if(ScoringSubsystem.globalTargetRotations < currentHeight)
        {
            elevatorMotor.set(-ELEVATOR_SPEED);
        }
        else
        {
            elevatorMotor.set(0); // Stop motor once at target
        }
    }

    public void moveToPosition(double targetRotations) //Moves the elevator to a given position
    {

        //Set target within min and max parameters
        if (targetRotations < MIN_ELEVATOR_HEIGHT)
        {
            targetRotations = MIN_ELEVATOR_HEIGHT;
        }
        else if (targetRotations > MAX_ELEVATOR_HEIGHT)
        {
            targetRotations = MAX_ELEVATOR_HEIGHT;
        }

        ScoringSubsystem.globalTargetRotations = targetRotations;

    }

    public void moveStep(boolean up) //Moves the elevator up or down to pre-set steps when bumpers are pressed
    {
        //Determines the current target position moves to an adjacent step position
        //Works based on the TARGET position, not the ACTUAL position
        //If you press to go up six times, it will go straight to the top without stopping.

        double currentHeight = elevatorEncoder.getDistance();
        double[] adjacent = new double[2];

        if()
        {

        }
        else if ()
        {

        }
    }

    public void moveGranular(boolean up) //Moves the elevator up or down when bumpers are pressed.
    {
        if(up)
        {
            moveToPosition(ScoringSubsystem.globalTargetRotations+1);
        }
        else
        {
            moveToPosition(ScoringSubsystem.globalTargetRotations+1);
        }
    }

    public void pull()
    {
        launcherMotor.set(1);
    }
    public void launch()
    {
        launcherMotor.set(-1);
    }

    public void stop() {
        // Stop the motor
        elevatorMotor.set(0);
        launcherMotor.set(0);
    }
}
