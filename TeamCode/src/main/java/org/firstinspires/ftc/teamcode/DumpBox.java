package org.firstinspires.ftc.teamcode;

/* Hanger controls all actions the intake arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "Hanger", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class DumpBox extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private static final String TAGDumpBox = "8492-DumpBox";

    private Servo SVRR = null;
    private Servo SVRL = null;

    public static enum BoxModes{
        BoxModes_In,
        BoxModes_Stop,
        BoxModes_Out,
    }



    private final double SVRR_IN = 1.0;
    private final double SVRR_OUT = 0.0;
    private final double SVRR_STOP = .5;

    private final double SVRL_IN = 0.0;
    private final double SVRL_OUT = 1.0;
    private final double SVRL_STOP = SVRR_STOP;



    private BoxModes boxMode_Current;
    private BoxModes boxMode_Desired;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        telemetry.addData("DumpBox", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
   }


    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }



    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("ServoStatus", boxMode_Current);


        if (boxMode_Current != boxMode_Desired) {

            boxMode_Current = boxMode_Desired;

            switch (boxMode_Current) {
                case BoxModes_In:
                    SVRL.setPosition(SVRL_IN);
                    SVRR.setPosition(SVRR_IN);
                    break;

                case BoxModes_Stop:
                    SVRL.setPosition(SVRL_STOP);
                    SVRR.setPosition(SVRR_STOP);
                    break;

                case BoxModes_Out:
                    SVRL.setPosition(SVRL_OUT);
                    SVRR.setPosition(SVRR_OUT);
                    break;
            }
        }
    }

    public void cmd_ServosIn(){
        boxMode_Desired = BoxModes.BoxModes_In;

    }

    public void cmd_ServosOut(){
        boxMode_Desired = BoxModes.BoxModes_Out;

    }

    public void cmd_ServosOff(){
        boxMode_Desired = BoxModes.BoxModes_Stop;

    }

    public BoxModes getServoMode() {

        return boxMode_Current;
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        SVRL.setPosition(SVRL_STOP);
        SVRR.setPosition(SVRR_STOP);

    }
}