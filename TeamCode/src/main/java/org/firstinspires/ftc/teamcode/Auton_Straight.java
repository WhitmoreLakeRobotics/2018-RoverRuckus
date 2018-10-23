package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_Straight ", group = "Auton")
// @Autonomous(...) is the other common choice

public class Auton_Straight extends OpMode {


    //declare and initialize stages
    private static final int stage0_preStart = 0;
    private static final int stage2_extened = 2;
    private static final int stage05_liftIntakAarm = 5;
    private static final int stage10_drive = 10;
    private static final int stage20_stop = 20;

    private int currentStage = stage0_preStart;

    // create instance of Chassis
    Chassis RBTChassis = new Chassis();
    private static final String TAGTeleop = "8492-Autonmous";
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;


    private double AUTO_DRIVEPower = .5;

    // declare auton power variables


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Auton_Straight", "Initialized");
        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.init();


        // initialize chassis with hardware map
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        // initialize chassis
        RBTChassis.init_loop();

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        // initialize chassis
        Runtime.getRuntime();
        RBTChassis.start();
        RBTChassis.setMotorMode_RUN_WITHOUT_ENCODER();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("Auto_Straight Stage", currentStage);
        RBTChassis.loop();

// check stage and do what's appropriate
        if (currentStage == stage0_preStart) {
            currentStage = stage2_extened;
        }
        if (currentStage == stage2_extened) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_EXNTENDED);
            currentStage = stage05_liftIntakAarm;
        }
        if (currentStage == stage05_liftIntakAarm) {
            currentStage = stage10_drive;
            RBTChassis.intakeArm.cmd_moveToCarryPos();
        }
        if (currentStage == stage10_drive) {
            if (RBTChassis.hanger.isExtended()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 50);
                currentStage = stage20_stop;  // error this was missing, so never stopped
            }
        }
        if (currentStage == stage20_stop) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_RETRACTED);
            if (RBTChassis.getcmdComplete()) {
                stop();
            }
        }

        // if (CurrentStage == stage_150Done) {
        //      if (robotChassis.getcmdComplete()) {
        if (runtime.seconds() > 29) {
            stop();
        }

        //}
        //}
    }  //  loop

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        //   robotChassis.stop();
        RBTChassis.stop();
    }

}
