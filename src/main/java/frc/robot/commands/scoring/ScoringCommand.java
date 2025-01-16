package frc.robot.commands.scoring;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.scoring.ScoringSubsystem;

public class ScoringCommand extends Command {
  private final ScoringSubsystem scoringSystem;
  private final double topRotation = 1000; //placeholder value, represents the rotations/degrees needed to reach the top

  private double targetPosition = 0; // target proportion of the full height, from 0 (bottom) to 1 (top)

  public ScoringCommand(ScoringSubsystem system, double target)
  {
    this.scoringSystem = system;
    this.targetPosition = target;
  }

  public void setTarget(short toggle) //toggle will be equal to either 1, 0, or -1.
  {
    if (toggle > 0)
    {
      targetPosition++;
    }
    else if (toggle < 0)
    {
      targetPosition--;
    }
    else
    {
      //Do not do anything.
    }
  }

  @Override
  public void execute() {
    scoringSystem.moveTo(topRotation*targetPosition) //placeholder command, please make something similar in ScoringSubsystem
  }

}
