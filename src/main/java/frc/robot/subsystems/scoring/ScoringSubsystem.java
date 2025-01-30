package frc.robot.subsystems.scoring;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ScoringSubsystem extends SubsystemBase {

    // Constants for motor and limits
    // All can be changable
    private static final int ELEVATOR_MOTOR_PWM_CHANNEL = 3; // PWM channel for the motor
    private static final int ENCODER_CHANNEL_A = 0; // Encoder channel A
    private static final int ENCODER_CHANNEL_B = 1; // Encoder channel B
    public static final double MAX_ELEVATOR_HEIGHT = 100.0; // Max encoder units (example)
    public static final double MIN_ELEVATOR_HEIGHT = 0.0;   // Min encoder units (example)
    private static final double JOYSTICK_DEADZONE = 0.1;     // Deadzone for joystick input
    private static final double ELEVATOR_SPEED = 0.5;        // Base speed for manual control

    // Motor, encoder, and joystick instances
    private final PWMSparkMax elevatorMotor = new PWMSparkMax(ELEVATOR_MOTOR_PWM_CHANNEL);
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

    public void moveTo(double targetRotations) {
        // Manually move elevator to a specific position
        double currentHeight = elevatorEncoder.getDistance();
        if (targetRotations >= MIN_ELEVATOR_HEIGHT && targetRotations <= MAX_ELEVATOR_HEIGHT) {
            if (targetRotations > currentHeight) {
                while (elevatorEncoder.getDistance() < targetRotations) {
                    elevatorMotor.set(ELEVATOR_SPEED);
                }
            } else if (targetRotations < currentHeight) {
                while (elevatorEncoder.getDistance() > targetRotations) {
                    elevatorMotor.set(-ELEVATOR_SPEED);
                }
            }
            elevatorMotor.set(0); // Stop motor once at target
        }
    }

    public void pull()
    {

    }
    public void launch()
    {

    }

    public void stop() {
        // Stop the motor
        elevatorMotor.set(0);
    }
}