// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.playingwithfusion.CANVenom;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final int front_right_motor_id = 12;

  private static final int front_left_motor_id = 16;

  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  private WPI_TalonSRX front_left_motor = new WPI_TalonSRX(front_left_motor_id);
  private WPI_VictorSPX front_right_motor = new WPI_VictorSPX(front_right_motor_id); // oddball motor controller
  private WPI_TalonSRX back_left_motor = new WPI_TalonSRX(15);
  private WPI_TalonSRX back_right_motor = new WPI_TalonSRX(14);

  private CANVenom elevatorMotor = new CANVenom(7);
  private boolean elevatorMotorMovementCommanded = false;

  private DifferentialDrive m_robotDrive;
  private Joystick joystick;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    back_left_motor.follow(front_left_motor);
    back_right_motor.follow(front_right_motor);

    // front_right_motor.setInverted(true);

    m_robotDrive = new DifferentialDrive(front_left_motor, front_right_motor);

    joystick = new Joystick(0);

  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled
    // commands, running already-scheduled commands, removing finished or
    // interrupted commands,
    // and running subsystem periodic() methods. This must be called from the
    // robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // drive controls
    boolean buttonA = joystick.getRawButton(1);
    if (buttonA) {
      m_robotDrive.arcadeDrive(joystick.getRawAxis(0) * 0.8, joystick.getRawAxis(1) * -0.8, true);
    } else {
      m_robotDrive.arcadeDrive(joystick.getRawAxis(0) * 0.65, -joystick.getRawAxis(1) * 0.65, true);
    }

    // elevator controls
    int POV = joystick.getPOV();
    switch(POV) {
      case -1:
      // not pressed
      elevatorMotor.set(0);
      elevatorMotorMovementCommanded = false;
      break;
      case 0:
      case 45:
      case 315:
      // up
      if (elevatorMotorMovementCommanded) {
        if ((elevatorMotor.getSpeed() == 0) && (elevatorMotor.getOutputCurrent() > 0)) {
          elevatorMotor.set(0);
          break;
        }
      }
      elevatorMotor.set(0.5);
      elevatorMotorMovementCommanded = true;
      break;
      case 180:
      case 225:
      case 135:
      //down
      if (elevatorMotorMovementCommanded) {
        if ((elevatorMotor.getSpeed() == 0) && (elevatorMotor.getOutputCurrent() > 0)) {
          elevatorMotor.set(0);
          break;
        }
      }
      elevatorMotor.set(-0.5);
      elevatorMotorMovementCommanded = true;
      break;
    }

    // intake controls

  }
  
  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}