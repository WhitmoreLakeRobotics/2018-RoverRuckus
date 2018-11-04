package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_Silver_Depot_Crater ", group = "Auton")
// @Autonomous(...) is the other common choice

public class Auton_Silver_Depot_Crater extends OpMode {


    //declare and initialize stages
    private static final int stage0_preStart = 0;
    private static final int stage10_extened = 10;
    private static final int stage20_liftIntakeAarm = 20;
    private static final int stage30_drive = 30;
    private static final int stage40_sample = 40;


    private static final int stage60_turn90 = 60;
    private static final int stage70_drive2Side = 70;
    private static final int stage80_turn2Depot = 80;
    private static final int stage85_drive2Depot = 85;
    private static final int stage90_Empty = 90;
    private static final int stage95_Backup2Crater = 95;
    private static final int stage99_stop = 99;
    private static final String TAGTeleop = "8492-Autonmous";
    // create instance of Chassis
    Chassis RBTChassis = new Chassis();
    private int currentStage = stage0_preStart;

    // declare auton power variables
    private double AUTO_DRIVEPower = .5;
    private double AUTO_TURNPower = .5;


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Auton_Depot_Crater", "Initialized");
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

        telemetry.addData("Auton_Depot_Cater", currentStage);
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
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 14);
                currentStage = stage40_sample;
            }
        }


        if (currentStage == stage40_sample) {
             currentStage = stage60_turn90;

        }


        if (currentStage == stage60_turn90) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_RETRACTED);
                RBTChassis.cmdTurn(-AUTO_TURNPower, AUTO_TURNPower, -75);
                currentStage = stage70_drive2Side;
            }
        }

        if (currentStage == stage70_drive2Side) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, -75, 46);
                currentStage = stage80_turn2Depot;
            }
        }

        if (currentStage == stage80_turn2Depot) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.cmdTurn(-AUTO_TURNPower, AUTO_TURNPower, -115);
                currentStage = stage85_drive2Depot;
            }
        }


        if (currentStage == stage85_drive2Depot) {
            if (RBTChassis.getcmdComplete()) {

                RBTChassis.cmdDrive(AUTO_DRIVEPower, -115, 36);
                currentStage = stage90_Empty;
            }
        }


        if (currentStage == stage90_Empty) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.dumpBox.cmd_ServoAutoOut(2000);
                currentStage = stage95_Backup2Crater;
            }
        }

        if (currentStage == stage95_Backup2Crater) {
            if (RBTChassis.dumpBox.getServoMode() == DumpBox.BoxModes.BoxModes_Stop ) {
                // driving backwards
                RBTChassis.cmdDrive(-AUTO_DRIVEPower, -135, -80);
                currentStage = stage99_stop;
            }
        }

        if (currentStage == stage99_stop) {
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
