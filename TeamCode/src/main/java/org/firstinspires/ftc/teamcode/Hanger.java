package org.firstinspires.ftc.teamcode;

/* Hanger controls all actions involving hanging from the Lander:
    - locking motors in break mode for initial hanger
    - moving the latch mechanism
    - lowering the chassis to the floor
    - retracking the hanger into the chassis
    - reversing this for the end game

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Device;


//@TeleOp(name = "Hanger", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Hanger extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private static final String TAGHanger = "8492-Hanger";


    /*
     cmd_MoveToTarget takes the new position in tick counts.

        It figures out if we need to move up with positive power or Down with negative power
        It then sets the new HANGERPOS_CmdPos and New HANGERPOWER_current
        It sets a boolean that we are underStickControl to false
        It does NOT set the motor Power... That will happen in the next loop if we are allowed
        to set the next power
        The buttons are used to very quickly move the HANGER to a given position with minimal
        overshoot or undershoot.

      cmdStickControl takes a double from the joystick position
         It simply sets the new power if it is legal value... AKA it limits
         the power to the valid powers that must be between HANGERPOWER_UP and HANGERPOWER_DOWN
         Stick control allows the driver to adjust and drive by eye


     */


    //Encoder positions for the HANGER
    public static final int RESTMODE = 0;
    public static final int EXTEND = 1;
    public static final int LATCHPOINT = 2;
    public static final int RETRACT = 3;
    public static final int HANGERMODE_EXTENDING = 4;
    public static final int HANGERMODE_EXTENDED = 5;
    public static final int HANGERMODE_RETRACTING = 6;
    public static final int HANGERMODE_RETRACTED = 7;
    public static final int HANGERMODE_LATCHPOINTING = 8;
    public static final int HANGERMODE_LATCHPOINTED = 9;
    public static final int HANGERRETRACTLIMIT = 0;
    public static final int HANGERPOSITIONTOLERANCE = 0;
    public static final int HANGEREXTENDLIMIT = 0;
    public static final int ticsPerRev = 1100;
    public static final double wheelDistPerRev = 4 * 3.14159;
    public static final double gearRatio = 80 / 80;
    public static final double ticsPerInch = ticsPerRev / wheelDistPerRev / gearRatio;

    // This is the current tick counts of the Hanger
    // This is the commanded tick counts of the Hanger

    public int hangerPosition_CURRENT = 0;

    //set the HANGER powers... We will need different speeds for up and down.
    private double currentMotorpower = 0.5;

    double HANGERStickDeadBand = .2;

// Boolean to check if movement complete


// boolean to check if auton or stick control


    // declare motors
    private Servo servoLatch = null;
    private DcMotor HM1 = null;
    private DcMotor HM2 = null;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // telemetry.addData("Status", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */


        HM1 = hardwareMap.dcMotor.get("HM1");
        HM2 = hardwareMap.dcMotor.get("HM2");

        HM1.setDirection(DcMotor.Direction.FORWARD);
        HM2.setDirection(DcMotor.Direction.FORWARD);
        HM1.setDirection(DcMotor.Direction.REVERSE);
        HM2.setDirection(DcMotor.Direction.REVERSE);


        HM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        HM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        HM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        HM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        HM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        servoLatch = hardwareMap.servo.get("servoLatch");


    }

    public void HangMotorEncoderReset() {


        HM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        HM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        HM2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    private void setMotorMode(DcMotor.RunMode newMode) {


        HM1.setMode(newMode);
        HM2.setMode(newMode);
    }

    public void setMotorMode_RUN_WITHOUT_ENCODER() {

        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


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
        //telemetry.addData("Status", "Running: " + runtime.toString());
        // RobotLog.aa(TAGHanger, "Curr Postion: " + Math.abs(HDM1.getCurrentPosition()));

//check if under stick control [must create process (public void ...) first]

    }

    private void SetMotorPower(double newMotorPower) {
        //Safety checks for the HANGER to prevent too low or too high

        if (getHANGERPOS_Ticks() <= HANGERPOSITIONTOLERANCE + HANGERRETRACTLIMIT){
            stop();
        }


        if (getHANGERPOS_Ticks() >= HANGERPOSITIONTOLERANCE - HANGEREXTENDLIMIT){
            stop();
        }

        // make sure that we do not attempt to move less than BOTTOM
        //if we are getting close to the bottom tolerance , slow down


        //if were within bottom tolerance, stop


        // make sure that we do not attempt a move greater than MAX


        // make sure that we are not going below the bottom
       /* if ((HANGERPOS_BOTTOM + HANGERPOS_TOL > HANGERPOS_current) && (HANGERPOWER_current < 0)) {
            newPower = 0;
        }

        // make sure that we are not going above the top
        if ((HANGERPOS_MAX - HANGERPOS_TOL < HANGERPOS_current) && (HANGERPOWER_current > 0)) {
            newPower = 0;
        }
*/
        //only set the power to the hardware when it is being changed.


    }

    private void testInPosition() {
        // tests if we are in position and stop if we are;
        //int curr_pos = HDM1.getCurrentPosition();

    }


    //driver is using stick control for Hanger
    public void cmdStickControl(double stickPos) {
/*
        if (Math.abs(stickPos) < HANGERStickDeadBand) {
            if (underStickControl) {
                HANGERPOWER_current = 0;
            }
            // we are inside the deadband do nothing.
            underStickControl = false;
            return;
        } else {
            underStickControl = true;
            cmdComplete = false;
            double currPower = stickPos;

            //limit the power of the stick
            if (stickPos > HANGERPOWER_UP) {
                currPower = HANGERPOWER_UP;
            }

            //limit the power of the stick
            if (stickPos < HANGERPOWER_DOWN) {
                currPower = HANGERPOWER_DOWN;
            }

            HANGERPOWER_current = currPower;
        }
        */
    }
    // create command to return to calling class (teleop / Auton) if move complete or not


    // somebody pressed a button or ran Auton to send command to move to a given location.
    // create new process


    //int curr_pos = HDM1.getCurrentPosition();


    //Do not move below BOTTOM


    //Do not move above MAX


    //we are higher than we want to be and
    //not already at the bottom.


    //We are lower than we want to be and not already at the top

    //We need to go down to target



        public double getHANGERPOS_Ticks() {

            int totalitics = Math.abs(HM1.getCurrentPosition()) +
                    Math.abs(HM2.getCurrentPosition()) ;
            double averagetics = totalitics / 2;
            double inches = averagetics / ticsPerInch;


            return inches;
        }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        SetMotorPower(0);

    }
}