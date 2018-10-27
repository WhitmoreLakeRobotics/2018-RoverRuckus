package org.firstinspires.ftc.teamcode;

/* Hanger controls all actions the intake arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "Hanger", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class DumpBox extends OpMode {
    private static final String TAGDumpBox = "8492-DumpBox";
    private final double SVRR_IN = -1;
    private final double SVRR_OUT = 1;
    private final double SVRR_STOP = 0;
    private final double SVRL_IN = 1;
    private final double SVRL_OUT = -1;
    private final double SVRL_STOP = SVRR_STOP;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private CRServo SVRR = null;
    private CRServo SVRL = null;
    private BoxModes boxMode_Current = BoxModes.BoxModes_Stop;
    private BoxModes boxMode_Desired = BoxModes.BoxModes_Stop;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        telemetry.addData("DumpBox", "Initialized");
        //ServoExtender = hardwareMap.crservo.get("Servo_Extender");
        SVRR = hardwareMap.crservo.get("dmpSvrr");
        SVRL = hardwareMap.crservo.get("dmpSvrl");
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
        boxMode_Current = BoxModes.BoxModes_Stop;
        boxMode_Desired = BoxModes.BoxModes_Stop;
        SVRL.setPower(SVRL_STOP);
        SVRR.setPower(SVRR_STOP);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("DumpBox", "In");


        if (boxMode_Current != boxMode_Desired) {

            boxMode_Current = boxMode_Desired;

            switch (boxMode_Current) {
                case BoxModes_In:
                    telemetry.addData("DumpBox", "In");
                    SVRL.setPower(SVRL_IN);
                    SVRR.setPower(SVRR_IN);
                    break;

                case BoxModes_Stop:
                    telemetry.addData("DumpBox", "Stop");
                    SVRL.setPower(SVRL_STOP);
                    SVRR.setPower(SVRR_STOP);
                    break;

                case BoxModes_Out:
                    telemetry.addData("DumpBox", "Out");
                    SVRL.setPower(SVRL_OUT);
                    SVRR.setPower(SVRR_OUT);
                    break;
            }
        }
    }

    public void cmd_ServosIn() {
        boxMode_Desired = BoxModes.BoxModes_In;

    }

    public void cmd_ServosOut() {
        boxMode_Desired = BoxModes.BoxModes_Out;

    }

    public void cmd_ServosOff() {
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
        SVRL.setPower(SVRL_STOP);
        SVRR.setPower(SVRR_STOP);

    }


    public static enum BoxModes {
        BoxModes_In,
        BoxModes_Stop,
        BoxModes_Out,
    }
}