package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.swervedrive.drivebase.AbsoluteDriveAdv;
import frc.robot.subsystems.scoring.ScoringSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import java.io.File;

public class RobotContainer {
    // Joysticks
    private final Joystick leftStick = new Joystick(0);
    private final Joystick rightStick = new Joystick(1);

    // Subsystems
    private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve/8387"));
    private final ScoringSubsystem scoringSubsystem = new ScoringSubsystem();

    AbsoluteDriveAdv closedAbsoluteDriveAdv = new AbsoluteDriveAdv(
        drivebase,
        () -> -MathUtil.applyDeadband(leftStick.getY(), OperatorConstants.LEFT_Y_DEADBAND),
        () -> -MathUtil.applyDeadband(leftStick.getX(), OperatorConstants.LEFT_X_DEADBAND),
        () -> -MathUtil.applyDeadband(rightStick.getX(), OperatorConstants.RIGHT_X_DEADBAND),
        leftStick::getTrigger,
        () -> false,
        () -> false,
        () -> false
    );

    Command driveFieldOrientedDirectAngle = drivebase.driveCommand(
        () -> MathUtil.applyDeadband(leftStick.getY() / 3.0, OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(leftStick.getX() / 3.0, OperatorConstants.LEFT_X_DEADBAND),
        () -> rightStick.getX(),
        () -> rightStick.getY()
    );

    Command driveFieldOrientedAngularVelocity = drivebase.driveCommand(
        () -> MathUtil.applyDeadband(leftStick.getY() * -1, OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(leftStick.getX() * -1, OperatorConstants.LEFT_X_DEADBAND),
        () -> rightStick.getX() * -1
    );

    Command driveFieldOrientedDirectAngleSim = drivebase.simDriveCommand(
        () -> MathUtil.applyDeadband(leftStick.getY(), OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(leftStick.getX(), OperatorConstants.LEFT_X_DEADBAND),
        () -> rightStick.getRawAxis(2)
    );

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        if (!DriverStation.isTest()) {
            new Trigger(leftStick::getTrigger).whileTrue(Commands.runOnce(() -> drivebase.setFullSpeedMode(true)))
                                              .onFalse(Commands.runOnce(() -> drivebase.setFullSpeedMode(false)));
            
            new Trigger(rightStick::getTrigger).whileTrue(Commands.runOnce(() -> scoringSubsystem.launch()));
            
            new Trigger(() -> rightStick.getRawButton(2)).whileTrue(Commands.runOnce(() -> scoringSubsystem.pull()));
            new Trigger(() -> rightStick.getRawButton(3)).whileTrue(Commands.runOnce(() -> scoringSubsystem.moveTo(0)));
            new Trigger(() -> rightStick.getRawButton(4)).whileTrue(Commands.runOnce(() -> scoringSubsystem.moveTo(25)));
            new Trigger(() -> rightStick.getRawButton(5)).whileTrue(Commands.runOnce(() -> scoringSubsystem.moveTo(50)));
            new Trigger(() -> rightStick.getRawButton(6)).whileTrue(Commands.runOnce(() -> scoringSubsystem.moveTo(100)));
        }
    }

    public Command getAutonomousCommand() {
        return drivebase.getAutonomousCommand("New Auto");
    }

    public void setDriveMode() {
        configureBindings();
    }

    public void setMotorBrake(boolean brake) {
        drivebase.setMotorBrake(brake);
    }
}
