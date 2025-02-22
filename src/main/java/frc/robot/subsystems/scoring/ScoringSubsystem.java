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
        double targetHeight = currentHeight + joystickValue * ELEVATOR_SPEED;

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

        //double currentHeight = elevatorEncoder.getDistance(); Shouldn't be needed because this works based on the target position
        double[] adjacent = new double[2];

        if(ScoringSubsystem.globalTargetRotations <= STEP_1) //If the target position is between the minimum and the first step, inclusive
        {
            adjacent[0] = MIN_ELEVATOR_HEIGHT;
            adjacent[1] = STEP_1;
        }
        else if (ScoringSubsystem.globalTargetRotations <= STEP_2) //If step 1 exclusive < target < step 2 inclusive
        {
            adjacent[0] = STEP_1;
            adjacent[1] = STEP_2;
        }
        else if (ScoringSubsystem.globalTargetRotations <= STEP_3) //If step 2 exclusive < target < step 3 inclusive
        {
            adjacent[0] = STEP_2;
            adjacent[1] = STEP_3;
        }
        else if (ScoringSubsystem.globalTargetRotations <= STEP_4) //If step 3 exclusive < target < step 4 inclusive
        {
            adjacent[0] = STEP_3;
            adjacent[1] = STEP_4;
        }
        else //If step 4 exclusive < target < maximum inclusive
        {
            adjacent[0] = STEP_4;
            adjacent[1] = MAX_ELEVATOR_HEIGHT;
        }

        //Move up or down to the adjacent step
        if(up)
        {
            moveToPosition(adjacent[1]);
        }
        else
        {
            moveToPosition(adjacent[0]);
        }
    }

    public void moveGranular(boolean up, double amount) //Moves the elevator up or down when bumpers are pressed.
    {
        if(up)
        {
            moveToPosition(ScoringSubsystem.globalTargetRotations+amount);
        }
        else
        {
            moveToPosition(ScoringSubsystem.globalTargetRotations-amount);
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