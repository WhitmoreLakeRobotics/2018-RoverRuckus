package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_test", group = "Auton")  // @Autonomous(...) is the other common choice

public class Auton_test extends OpMode {


    //declare and initialize stages
    private static final int stage0_preStart = 0;
    private static final int stage1_drive = 1;
    private static final int stage2_stop = 2;

    int current = stage0_preStart;

    // create instance of Chassis
    Chassis RBTChassis = new Chassis();
    private static final String TAGTeleop = "8492-Autonmous";
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;


    private double AUTO_DRIVEPower = .3;

    // declare auton power variables


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        RBTChassis.hardwareMap = hardwareMap;
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

        // telemetry.addData("Stage", CurrentStage);
        // initialize chassis
        RBTChassis.loop();

// check stage and do what's appropriate
        if (current == stage0_preStart) {
            current = stage1_drive;
        }
        if (current == stage1_drive) {
            RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 96);
            current = stage2_stop;  // error this was missing, so never stopped
        }
        if (current == stage2_stop) {

        }

        // if (CurrentStage == stage_150Done) {
        //      if (robotChassis.getcmdComplete()) {
        if (runtime.seconds() > 25) {
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
