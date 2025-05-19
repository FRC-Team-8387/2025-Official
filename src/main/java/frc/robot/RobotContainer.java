// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import java.io.File;
import java.lang.reflect.Method;
import java.util.function.DoubleSupplier;

import swervelib.SwerveInputStream;
import frc.robot.commands.scoring.ScoringCommand;
import frc.robot.subsystems.scoring.ScoringSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{
  double baseinvert = 1;
  double invert;

  //deadband utility method
  private double applyDeadzoneDouble(double input)
  {
    if(input > -0.05 && input < 0.05)
    {
      input = 0;
    }
    return input;
  }

  private final SendableChooser<Command> autoChooser;
  // Replace with CommandPS4Controller or CommandJoystick if needed
  final         CommandXboxController driverXbox = new CommandXboxController(0);

 

  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem       drivebase  = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
                                                                                "swerve"));
  
  public final ScoringSubsystem scoringSystem = new ScoringSubsystem();
  /**
   * Converts driver input into a field-relative ChassisSpeeds that is controlled by angular velocity.
   */
  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(),
                                                                () -> applyDeadzoneDouble(driverXbox.getLeftY()) * 1,
                                                                () -> applyDeadzoneDouble(driverXbox.getLeftX()) * 1)
                                                                //() -> driverXbox.getLeftX() * 1) 
                                                                //! inverted forwards/backwards movement to solve issues with robot-oriented
                                                                //Not sure why those issues were there
                                                                //nvm that approach is really stupid that would invert angles
                                                            .withControllerRotationAxis(driverXbox::getRightX)
                                                            .deadband(OperatorConstants.DEADBAND)
                                                            .scaleTranslation(0.8)
                                                            .allianceRelativeControl(true);

  /**
   * Clone's the angular velocity input stream and converts it to a fieldRelative input stream.
   */
  SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(driverXbox::getRightX,
                                                                                             driverXbox::getRightY)
                                                           .headingWhile(true);

  /**
   * Clone's the angular velocit
   y input stream and converts it to a robotRelative input stream.
   */
  SwerveInputStream driveRobotOriented = driveAngularVelocity.copy().robotRelative(true)
                                                             .allianceRelativeControl(false);

  SwerveInputStream driveAngularVelocityKeyboard = SwerveInputStream.of(drivebase.getSwerveDrive(),
                                                                        () -> -driverXbox.getLeftY(),
                                                                        () -> -driverXbox.getLeftX())
                                                                    .withControllerRotationAxis(() -> driverXbox.getRawAxis(
                                                                        2))
                                                                    .deadband(OperatorConstants.DEADBAND)
                                                                    .scaleTranslation(0.8)
                                                                    .allianceRelativeControl(true);
  // Derive the heading axis with math!
  SwerveInputStream driveDirectAngleKeyboard     = driveAngularVelocityKeyboard.copy()
                                                                               .withControllerHeadingAxis(() ->
                                                                                                              Math.sin(
                                                                                                                  driverXbox.getRawAxis(
                                                                                                                      2) *
                                                                                                                  Math.PI) *
                                                                                                              (Math.PI *
                                                                                                               2),
                                                                                                          () ->
                                                                                                              Math.cos(
                                                                                                                  driverXbox.getRawAxis(
                                                                                                                      2) *
                                                                                                                  Math.PI) *
                                                                                                              (Math.PI *
                                                                                                               2))
                                                                               .headingWhile(true)
                                                                               .translationHeadingOffset(true)
                                                                               .translationHeadingOffset(Rotation2d.fromDegrees(
                                                                                   0));

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer()
  {
    
    NamedCommands.registerCommand("Move Elevator Up", new RunCommand(() -> scoringSystem.MoveElevatorMotorToPosition(Constants.LEVEL_3)));
    NamedCommands.registerCommand("Launch", new RunCommand(() -> scoringSystem.launchCommand()).withTimeout(0.5));
    NamedCommands.registerCommand("Stop", new RunCommand(() -> scoringSystem.stopCommand()));

    autoChooser = AutoBuilder.buildAutoChooser();
    // Configure the trigger bindings
    configureBindings();
    DriverStation.silenceJoystickConnectionWarning(true);
    NamedCommands.registerCommand("test", Commands.print("I EXIST"));
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
    
    Command driveFieldOrientedDirectAngle      = drivebase.driveFieldOriented(driveDirectAngle);
    Command driveFieldOrientedAnglularVelocity = drivebase.driveFieldOriented(driveAngularVelocity);
    Command driveRobotOrientedAngularVelocity  = drivebase.driveFieldOriented(driveRobotOriented);
    Command driveSetpointGen = drivebase.driveWithSetpointGeneratorFieldRelative(
        driveDirectAngle);
    Command driveFieldOrientedDirectAngleKeyboard      = drivebase.driveFieldOriented(driveDirectAngleKeyboard);
    Command driveFieldOrientedAnglularVelocityKeyboard = drivebase.driveFieldOriented(driveAngularVelocityKeyboard);
    Command driveSetpointGenKeyboard = drivebase.driveWithSetpointGeneratorFieldRelative(
        driveDirectAngleKeyboard);


        //zdriverXbox.a().onTrue(scoringSystem.ElevatorLevelOne());
    /*
    if (RobotBase.isSimulation())
    {
      drivebase.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
    } else
    {
      drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    }
    */
    drivebase.setDefaultCommand(driveRobotOrientedAngularVelocity);

    /* bs bindings f all of this
    driverXbox.rightTrigger()
      .and(driverXbox.rightStick())
      .whileTrue(Commands.run(() -> scoringSystem.moveGranularCommand(true, scoringSystem.getSpeed()))); //figure out how to base it on how far the trigger's been pressed
    driverXbox.leftTrigger()
      .and(driverXbox.rightStick())
      .whileTrue(Commands.run(() -> scoringSystem.moveGranularCommand(false, scoringSystem.getSpeed()))); //figure out how to base it on how far the trigger's been pressed
    */

    //By default, if the triggers are pressed, step the elevator up or down
    //driverXbox.rightTrigger().onTrue(Commands.runOnce(() ->  scoringSystem.moveStepCommand(true)));
    //driverXbox.leftTrigger().onTrue(Commands.runOnce(() ->  scoringSystem.moveStepCommand(false)));

    //If left button is pressed, pull the game piece in.
    //driverXbox.leftBumper().whileTrue(Commands.run(() -> scoringSystem.pullCommand()));

    //If right button is pressed, launch the game piece out
    //driverXbox.rightBumper().whileTrue(Commands.run(() -> scoringSystem.launchCommand()));

    // driverXbox.rightTrigger(0.05).whileTrue(new InstantCommand(() -> scoringSystem.moveElevatorUp(driverXbox.getRightTriggerAxis() * 3)));
    // driverXbox.rightTrigger().or(driverXbox.leftTrigger()).onFalse(new InstantCommand(() -> scoringSystem.stopElevator()));
    // driverXbox.leftTrigger(0.05).whileTrue(new InstantCommand(() -> scoringSystem.moveElevatorDown(driverXbox.getLeftTriggerAxis() * 3)));

    //ELEVATOR CONTROLS: A for level 1, B for level 2, Y for level 3, X to reset
    //NOTE: RESET 0 TO THE LOWEST POSSIBLE POSITION EVERY TIME YOU RESTART
    driverXbox.a().onTrue(new InstantCommand(() -> scoringSystem.MoveElevatorMotorToPosition(Constants.LEVEL_1)));
    driverXbox.b().onTrue(new InstantCommand(() -> scoringSystem.MoveElevatorMotorToPosition(Constants.LEVEL_2)));
    driverXbox.y().onTrue(new InstantCommand(() -> scoringSystem.MoveElevatorMotorToPosition(Constants.LEVEL_3)));
    driverXbox.x().onTrue(new InstantCommand(() -> scoringSystem.ResetEncoders()));

    


    //If the left joystick is pressed, toggle to double the speed (otherwise halve it)
    //driverXbox.leftStick().toggleOnTrue(Commands.runOnce(null)); Worry about this later, we haven't made the logic for it yet.

    /*
     * NOTE FROM JOSEPH (V): THE CORRECT SYNTAX FOR CALLING A COMMAND BASED ON A CONTROL IS:
     * 
     * controllerName.buttonName().onTrue/whileTrue/onFalse/whileFalse(Commands.runOnce/run(() -> subSystem.commandName(parameters)));
     */
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
    // An example command will be run in autonomous
    //original:
    //return drivebase.getAutonomousCommand("New Auto");
    
    //Placeholder values, distances in meters
    double metersPerSecond = 1;
    double distanceToReef = 1.5;
    // double distanceBack = 1;
    // double distanceToBarge_Left = 2;
    // double distanceToBarge_Forwards = 5;

    //ATTENTION: THIS CODE REQUIRES US TO BE IN THE *CENTER* STARTING POSITION.
    return drivebase.driveTimeCommand((distanceToReef/metersPerSecond), -metersPerSecond) //Drive to reef
            //.andThen(scoringSystem.MoveElevatorMotorToPosition(Constants.LEVEL_1))
            .andThen(scoringSystem.moveElevatorUpCommand(1).withTimeout(5))
            //.andThen(scoringSystem.stopCommand()) //I think we actually might not need these stop commands?
            .andThen(scoringSystem.launchCommand()).withTimeout(5); //Deposit coral
            //.andThen(scoringSystem.stopCommand());
            // .andThen(drivebase.driveToDistanceCommand(-1 * distanceBack,metersPerSecond)) //Drive back
            // .andThen(scoringSystem.stopCommand()) //Stop the launcher
            // .andThen(drivebase.driveCommandWithDoubles(0,distanceToBarge_Left,0)) //Move left
            // .andThen(][ivebase.driveToDistanceCommand(distanceToBarge_Forwards,metersPerSecond)); //Move to barge
    //return autoChooser.getSelected();

    /*
     * PLAN:
     *  - Drive forwards
     *  - Deposit a coral *somewhere* on the reef
     *  - Drive to wherever we need to after that
     * PROBLEMS:
     *  - How do we do that
     * ADDENDUM:
     *  - I think I did it
     */
  }

  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }
}
