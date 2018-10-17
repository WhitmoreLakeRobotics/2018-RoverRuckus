package org.firstinspires.ftc.teamcode;

/* Hanger controls all actions the intake arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.sun.tools.javac.code.Attribute;


//@TeleOp(name = "Hanger", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class IntakeArm extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private static final String TAGIntakeArm = "8492-IntakeArm";


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

    public static enum IntakeDestinations{
        IntakeDestinations_Pickup,
        IntakeDestinations_Carry,
        IntakeDestinations_Dump,
        IntakeDestinations_StickControl,
        IntakeDestinations_Unknown
    }

    //Encoder positions for the IntakeArm
    public static final int IntakePos_Tol = 70;
    public static final int IntakePos_Pickup = 0;
    public static final int IntakePos_Dump = 3670;
    public static final int IntakePos_Carry = Math.round(IntakePos_Dump /2);

    public static final double IntakePowerDown = -.35;
    public static final double IntakePowerUp = 0.75;
    public static final double IntakePowerInit = -0.20;
    double IntakePowerCurrent = 0;
    double IntakePowerDesired = 0;
    boolean cmdComplete = false;



    int IntakePosCurrent = IntakePos_Pickup;
    private IntakeDestinations current_Destination = IntakeDestinations.IntakeDestinations_Pickup;

    //set the powers... We will need different speeds for up and down.



    double IntakeStickDeadBand = 1;


    private DcMotor AM1 = null;
    private DigitalChannel ArmTCH = null;



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
        // get a reference to our digitalTouch object.

        ArmTCH = hardwareMap.get(DigitalChannel.class,"ArmTCH");
        ArmTCH.setMode(DigitalChannel.Mode.INPUT);
        AM1 = hardwareMap.dcMotor.get("AM1");
        AM1.setDirection(DcMotor.Direction.REVERSE);
        AM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        AM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        AM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void MotorEncoderReset() {
        AM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        AM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        //initArmTCH();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("IntakeArmPower", IntakePowerDesired);

        //check if under stick control [must create process (public void ...) first]
        //IntakeArmSafetyChecks();
        SetMotorPower(IntakePowerDesired);

    }

    private void initArmTCH (){
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        runtime.startTime();

        AM1.setPower(IntakePowerInit);
        while (ArmTCH.getState() == false) {
            if (runtime.milliseconds() > 2000) {
                break;
            }
        }
        SetMotorPower(0.0);
    }
    private void IntakeArmSafetyChecks(){

        if (inPosition_Tol(IntakePos_Pickup) && IntakePowerDesired < 0){
            IntakePowerDesired = 0;
            current_Destination = IntakeDestinations.IntakeDestinations_Pickup;
        }
        else if (inPosition_Tol(IntakePos_Pickup) && IntakePowerDesired > 0) {
            IntakePowerDesired = 0;
            current_Destination = IntakeDestinations.IntakeDestinations_Dump;
        }
    }


    private boolean inPosition_Tol (int PositionTicks){
        // Tests if current position is within positional tolerance
        boolean retValue = false;
        if (Math.abs(IntakePosCurrent - PositionTicks) < Math.abs(IntakePos_Tol)){
            retValue = true;
        }
        return retValue;
    }

    public boolean atDestination (IntakeDestinations desiredDestination){
        // Tests if we are at desired named destination
        boolean retValue = false;

        switch (desiredDestination) {
            case IntakeDestinations_Pickup:
                retValue = inPosition_Tol(IntakePos_Pickup);
                break;

            case IntakeDestinations_Carry:
                retValue = inPosition_Tol(IntakePos_Carry);
                break;

            case IntakeDestinations_Dump:
                retValue = inPosition_Tol(IntakePos_Dump);
                break;


            case IntakeDestinations_StickControl:
                if (inPosition_Tol(IntakePos_Pickup) && IntakePowerDesired < 0) {
                    retValue = true;
                }
                else if (inPosition_Tol(IntakePos_Dump) && IntakePowerDesired > 0) {
                    retValue = true;
                }
                else {
                    retValue = false;
                }
                break;
            case IntakeDestinations_Unknown:
                retValue = true;
                break;
            default:
                retValue = true;
                break;

        }

        return retValue;
    }

    private void SetMotorPower(double newMotorPower) {
        //set the motors for the intake Arm to the new power only after
        // Safety checks to prevent too low or too high
        IntakePosCurrent = AM1.getCurrentPosition();

        double newPower = newMotorPower;

        if (atDestination(current_Destination)){
            newPower = 0;
        }

        //only set the power to the hardware when it is being changed.
        if (newPower != IntakePowerCurrent ){
            IntakePowerCurrent = newPower;
            IntakePowerDesired = newPower;
            AM1.setPower(IntakePowerCurrent);
        }
    }

    //driver is using stick control for Hanger
    public void cmd_StickControl(double stickPos) {

        if (Math.abs(stickPos) < Math.abs(IntakeStickDeadBand)) {
            if (current_Destination ==  IntakeDestinations.IntakeDestinations_StickControl) {
                IntakePowerDesired = 0;
                current_Destination = IntakeDestinations.IntakeDestinations_Unknown;
            }
            return;
        } else {
            current_Destination = IntakeDestinations.IntakeDestinations_StickControl;

            double currPower = stickPos;

            //limit the power of the stick
            if (currPower > IntakePowerUp) {
                currPower = IntakePowerUp;
            }

            //limit the power of the stick
            if (currPower < IntakePowerDown) {
                currPower = IntakePowerDown;
            }

            IntakePowerDesired = currPower;
        }
    }

    public void cmd_moveToPickupPos(){

        current_Destination = IntakeDestinations.IntakeDestinations_Pickup;
        if (atDestination(current_Destination)) {
            IntakePowerDesired = 0;
        }
        else{
            IntakePowerDesired = IntakePowerDown;
        }

    }

    public void cmd_moveToDumpPos(){
        current_Destination = IntakeDestinations.IntakeDestinations_Dump;
        if (atDestination(current_Destination)) {
            IntakePowerDesired = 0;
        }
        else {
            IntakePowerDesired = IntakePowerUp;
        }

    }

    public void cmd_moveToCarryPos(){
        current_Destination = IntakeDestinations.IntakeDestinations_Carry;
        if (atDestination(current_Destination)) {
            //we are at carry Pos   Stop Now
            IntakePowerDesired = 0;
        }
        else if(IntakePosCurrent > IntakePos_Carry){
            IntakePowerDesired = IntakePowerDown;
        }
        else if (IntakePosCurrent < IntakePos_Carry){
            IntakePowerDesired = IntakePowerUp;
        }

    }

    public int getPOS_Ticks() {
        return IntakePosCurrent;
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        SetMotorPower(0.0);

    }
}