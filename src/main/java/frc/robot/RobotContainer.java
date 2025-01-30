package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.scoring.ScoringSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import java.io.File;

public class RobotContainer {

  // Replace Xbox controller with Logitech Extreme 3D Pro joystick
  final CommandJoystick driverJoystick = new CommandJoystick(0); // Ensure the joystick port matches Driver Station configuration
  private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve/8387"));
  private final ScoringSubsystem scoringSubsystem = new ScoringSubsystem();

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    if (DriverStation.isTest()) {
      driverJoystick.button(1).whileTrue(Commands.run(scoringSubsystem::stop, scoringSubsystem)); // Trigger to stop elevator
      driverJoystick.button(3).whileTrue(Commands.run(() -> scoringSubsystem.moveTo(100), scoringSubsystem)); // Button 3 moves up, verify max height
      driverJoystick.button(2).whileTrue(Commands.run(() -> scoringSubsystem.moveTo(0), scoringSubsystem)); // Button 2 moves down, verify min height
    } else {
      driverJoystick.button(1).onTrue(Commands.runOnce(drivebase::zeroGyro));
      driverJoystick.button(4).whileTrue(Commands.run(() -> scoringSubsystem.moveTo(50), scoringSubsystem)); // Button 4 - Mid height, ensure 50 is correct for mid-position
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


