package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_Silver_Crater ",  group = "Auton")
// @Autonomous(...) is the other common choice

public class Auton_Silver_Crater extends OpMode {


    //declare and initialize stages
    private static final int stage0_preStart = 0;
    private static final int stage2_extened = 2;
    private static final int stage10_drive = 10;
    private static final int stage15_hangerDown = 15;
    private static final int stage20_stop = 20;
    private static final String TAGTeleop = "8492-Auton_Gold_Crater";
    // create instance of Chassis
    Chassis RBTChassis = new Chassis();
    private int currentStage = stage0_preStart;


    private double AUTO_DRIVEPower = .9;


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Auton_Gold_Crater", "Initialized");
        RBTChassis.setParentMode(Chassis.PARENTMODE.PARENT_MODE_AUTO);
        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.init();
        msStuckDetectStart = 8000;

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

        telemetry.addData("Auto_Gold_Crater", currentStage);
        RBTChassis.loop();

        // check stage and do what's appropriate
        if (currentStage == stage0_preStart) {
            currentStage = stage2_extened;
        }
        if (currentStage == stage2_extened) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_EXNTENDED);
            RBTChassis.intakeArm.cmd_moveToCarryPos();
            currentStage = stage10_drive;
        }

        if (currentStage == stage10_drive) {
            if (RBTChassis.hanger.isExtended()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 56);
                currentStage = stage15_hangerDown;
            }
        }

        if (currentStage == stage15_hangerDown) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_RETRACTED);
                currentStage = stage20_stop;
            }
        }

        if (currentStage == stage20_stop) {
            if (RBTChassis.hanger.isRetracted()) {
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


