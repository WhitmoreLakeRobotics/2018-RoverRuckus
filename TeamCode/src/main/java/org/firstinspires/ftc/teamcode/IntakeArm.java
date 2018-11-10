package org.firstinspires.ftc.teamcode;

/* controls all actions the intake arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class IntakeArm extends OpMode {
    //Encoder positions for the IntakeArm
    public static final int IntakePos_Tol = 70;
    public static final int IntakePos_Pickup = 0;
    public static final int IntakePos_Dump = 3670;
    public static final int IntakePos_Carry = Math.round(IntakePos_Dump / 3);
    public static final double IntakePowerDown = -.35;
    public static final double IntakePowerUp = 0.80;
    public static final double IntakePowerInit = -0.30;
    private static final String TAGIntakeArm = "8492-IntakeArm";
    double IntakePowerCurrent = 0;
    double IntakePowerDesired = 0;
    boolean cmdComplete = false;
    int IntakePosCurrent = IntakePos_Pickup;
    double IntakeStickDeadBand = 1;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private Hanger hanger = null;
    private IntakeDestinations desiredDestination = IntakeDestinations.IntakeDestinations_Pickup;

    //set the powers... We will need different speeds for up and down.
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

        ArmTCH = hardwareMap.get(DigitalChannel.class, "ArmTCH");
        ArmTCH.setMode(DigitalChannel.Mode.INPUT);
        AM1 = hardwareMap.dcMotor.get("AM1");
        AM1.setDirection(DcMotor.Direction.REVERSE);
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

    }

    public void autoStart() {
        initArmTCH();
        AM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        AM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void teleStart() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("IntakeArmPower", IntakePowerDesired);

        //check if under stick control [must create process (public void ...) first]
        // IntakeArmSafetyChecks();
        SetMotorPower(IntakePowerDesired);

    }

    private void initArmTCH() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        //runtime.startTime();

        AM1.setPower(IntakePowerInit);
        while (ArmTCH.getState()) {
            if (runtime.milliseconds() > 2000) {
                break;
            }
        }
        AM1.setPower(0);
    }

    private void IntakeArmSafetyChecks() {

        // test if we are down.
        if ((inPosition_Tol(IntakePos_Pickup, IntakePosCurrent, IntakePos_Tol) && (IntakePowerDesired < 0)) ||
                (!ArmTCH.getState() && (IntakePowerDesired < 0))) {

            IntakePowerDesired = 0;
            desiredDestination = IntakeDestinations.IntakeDestinations_Pickup;

        } else if (inPosition_Tol(IntakePos_Pickup, IntakePosCurrent, IntakePos_Tol) && IntakePowerDesired > 0) {

            IntakePowerDesired = 0;
            desiredDestination = IntakeDestinations.IntakeDestinations_Dump;

        }
    }

    private boolean inPosition_Tol(int dest, int currPos, int tol) {
        // Tests if current position is within positional tolerance
        boolean retValue = false;
        if (Math.abs(dest - currPos) < Math.abs(tol)) {
            retValue = true;
        }
        return retValue;
    }

    public boolean atDestination(IntakeDestinations desiredDestination) {
        // Tests if we are at desired named destination
        boolean retValue = false;

        switch (desiredDestination) {
            case IntakeDestinations_Pickup:
                retValue = inPosition_Tol(IntakePos_Pickup, IntakePosCurrent, IntakePos_Tol);
                // we are on the switch we are in Pickup pos
                //if (!ArmTCH.getState()) {
                //    retValue = true;
                //}
                // We are below pickup... likely only by a few ticks but call it in position
                // and cause a stop to happen
                if (IntakePosCurrent < IntakePos_Pickup) {
                    retValue = true;
                }
                break;

            case IntakeDestinations_Carry:
                retValue = inPosition_Tol(IntakePos_Carry, IntakePosCurrent, (int)(IntakePos_Tol * 1.4));
                break;

            case IntakeDestinations_Dump:
                retValue = inPosition_Tol(IntakePos_Dump, IntakePosCurrent, IntakePos_Tol);

                if (IntakePosCurrent > IntakePos_Dump) {
                    retValue = true;
                }
                break;


            case IntakeDestinations_StickControl:
                // Stick control only stops at Pickup and Dump positions.

                if (inPosition_Tol(IntakePos_Pickup, IntakePosCurrent, IntakePos_Tol) && IntakePowerDesired < 0) {
                    retValue = true;

                } else if (!ArmTCH.getState()) {
                    retValue = true;

                } else if ((IntakePosCurrent < IntakePos_Pickup) && (IntakePowerDesired < 0)) {
                    retValue = true;

                } else if (inPosition_Tol(IntakePos_Dump, IntakePosCurrent, IntakePos_Tol) && IntakePowerDesired > 0) {
                    retValue = true;

                } else if ((IntakePosCurrent > IntakePos_Dump) && (IntakePowerDesired > 0)) {
                    retValue = true;

                } else {
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
        cmdComplete = retValue;
        return retValue;
    }

    private void SetMotorPower(double newMotorPower) {
        //set the motors for the intake Arm to the new power only after
        // Safety checks to prevent too low or too high
        IntakePosCurrent = AM1.getCurrentPosition();

        double newPower = newMotorPower;

        if (atDestination(desiredDestination)) {
            newPower = 0;
        }

        if (!hanger.isRetracted()) {
            newPower = 0;
        }

        //only set the power to the hardware when it is being changed.
        if (newPower != IntakePowerCurrent) {
            IntakePowerCurrent = newPower;
            IntakePowerDesired = newPower;
            AM1.setPower(IntakePowerCurrent);
        }
    }

    //driver is using stick control for Intake Arm
    public void cmd_StickControl(double stickPos) {

        if (Math.abs(stickPos) < Math.abs(IntakeStickDeadBand)) {
            if (desiredDestination == IntakeDestinations.IntakeDestinations_StickControl) {
                IntakePowerDesired = 0;
                desiredDestination = IntakeDestinations.IntakeDestinations_Unknown;
            }
            return;
        } else {
            desiredDestination = IntakeDestinations.IntakeDestinations_StickControl;

            double currPower = stickPos;

            //limit the power of the stick
            if (currPower > IntakePowerUp) {
                currPower = IntakePowerUp;
            }

            //Make sure the hanger is Down  AKA no going up unless the hanger is down
            if (!hanger.isRetracted()) {
                currPower = 0;
            }

            //limit the power of the stick
            if (currPower < IntakePowerDown) {
                currPower = IntakePowerDown;
            }


            IntakePowerDesired = currPower;
        }
    }

    public void cmd_moveToPickupPos() {

        desiredDestination = IntakeDestinations.IntakeDestinations_Pickup;
        if (atDestination(desiredDestination)) {
            IntakePowerDesired = 0;
        } else {
            IntakePowerDesired = IntakePowerDown;
        }

    }

    public void cmd_moveToDumpPos() {
        desiredDestination = IntakeDestinations.IntakeDestinations_Dump;
        if (atDestination(desiredDestination)) {
            IntakePowerDesired = 0;
        } else {
            IntakePowerDesired = IntakePowerUp;
        }

    }

    public void cmd_moveToCarryPos() {
        desiredDestination = IntakeDestinations.IntakeDestinations_Carry;
        if (atDestination(desiredDestination)) {
            //we are at carry Pos   Stop Now
            IntakePowerDesired = 0;
        } else if (IntakePosCurrent > IntakePos_Carry) {
            IntakePowerDesired = IntakePowerDown;
        } else if (IntakePosCurrent < IntakePos_Carry) {
            IntakePowerDesired = IntakePowerUp;
        }

    }

    public void setHanger(Hanger hangR) {
        hanger = hangR;
    }

    public int getPOS_Ticks() {
        return IntakePosCurrent;
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        AM1.setPower(0.0);
    }


    public static enum IntakeDestinations {
        IntakeDestinations_Pickup,
        IntakeDestinations_Carry,
        IntakeDestinations_Dump,
        IntakeDestinations_StickControl,
        IntakeDestinations_Unknown
    }
}