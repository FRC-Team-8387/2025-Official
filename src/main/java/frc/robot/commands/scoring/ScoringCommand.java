package frc.robot.commands.scoring;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.scoring.ScoringSubsystem;

public class ScoringCommand extends Command {
  private final ScoringSubsystem scoringSystem;
  private final int movementVal;

  //private double targetPosition = 0; // target proportion of the full height, from 0 (bottom) to 1 (top)

  public ScoringCommand(ScoringSubsystem system, int movementVal)
  {
    this.scoringSystem = system;
    this.movementVal = movementVal;
    //this.targetPosition = target;
    addRequirements(scoringSystem);
  }

  public void execute()
  {
    scoringSystem.set(movementVal);
  }

  /*
   * So previously we were planning on having pre-set positions, 
   * but this won't actually work because the NEO motor with the 
   * SparkMax controller can't be set to go to specific positions.
   * It can only be set to go to at *speeds,* not *positions.*
   * So elevator movement will have to be dynamic, unless we can find some other solution.
   * Which we won't.
   */



}
