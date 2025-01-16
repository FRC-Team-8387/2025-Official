package frc.robot.commands.scoring;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.scoring.ScoringSubsystem;

public class ScoringCommand extends Command {
  private final ScoringSubsystem scoringSystem;
  final double topRotation = 1000; //placeholder value, represents the rotations/degrees needed to reach the top

  double targetPosition; // target proportion of the full height, from 0 (bottom) to 1 (top)

  public ScoringCommand(ScoringSubsystem system, double target)
  {
      this.scoringSystem = system;
      this.targetPosition = target;
  }

  @Override
  public void execute() {
    scoringSystem.moveTo(topRotation*targetPosition) //placeholder command, please make something similar in ScoringSubsystem
  }

}
