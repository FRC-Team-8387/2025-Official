package frc.robot.commands.scoring;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.scoring.ScoringSubsystem;

public class ScoringCommand extends Command {
  private final ScoringSubsystem scoringSystem;
  private final double targetRotations;

  public ScoringCommand(ScoringSubsystem system, double target)
  {
    this.scoringSystem = system;
    this.targetRotations = target;
    //this.targetPosition = target;
    addRequirements(scoringSystem);
  }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
      System.out.println("Elevator movement beginning");
    }
  
     // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
  
      scoringSystem.moveTo(targetRotations);
    }
  
    
  
    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
      scoringSystem.moveTo(0);
      System.out.println("Elevator movement ended; return to start");
    }
  
    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
      // leave false, so always can move
      return false;
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
