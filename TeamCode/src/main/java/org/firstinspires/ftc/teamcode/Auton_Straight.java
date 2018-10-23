package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_Straight ", group = "Auton")
// @Autonomous(...) is the other common choice

public class Auton_Straight extends OpMode {


    //declare and initialize stages
    private static final int stage0_preStart = 0;
    private static final int stage10_extened = 10;
    private static final int stage20_liftIntakeAarm = 20;
    private static final int stage30_drive = 30;
    private static final int stage99_stop = 99;

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
        RBTChassis.setParentMode(Chassis.PARENTMODE.PARENT_MODE_AUTO);
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
            currentStage = stage10_extened;
        }


        if (currentStage == stage10_extened) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_EXNTENDED);
            currentStage = stage20_liftIntakeAarm;
        }


        if (currentStage == stage20_liftIntakeAarm) {
            currentStage = stage30_drive;
            RBTChassis.intakeArm.cmd_moveToCarryPos();
        }


        if (currentStage == stage30_drive) {
            if (RBTChassis.hanger.isExtended()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 50);
                currentStage = stage99_stop;  // error this was missing, so never stopped
            }
        }


        if (currentStage == stage99_stop) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_RETRACTED);
            if (RBTChassis.getcmdComplete()) {
                stop();
            }
        }

        if (runtime.seconds() > 29) {
            stop();
        }

    }  //  loop

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        RBTChassis.stop();
    }

}
