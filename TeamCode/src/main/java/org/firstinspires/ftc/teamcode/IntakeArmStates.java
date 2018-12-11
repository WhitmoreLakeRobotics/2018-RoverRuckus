package org.firstinspires.ftc.teamcode;

/* controls all actions the intake arm

 */


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class IntakeArmStates extends BaseHardware {
    //Encoder positions for the IntakeArm
    public static final int IntakePivotPos_Tol = 70;

    public static final int IntakePivotPos_Start = 0;
    public static final int IntakePivotPos_ExtPickup = 1450;
    public static final int IntakePivotPos_BottomThrottle = 2700;
    public static final int IntakePivotPos_BottomBrake = 1600;
    public static final int IntakePivotPos_Carry = 2500;
    public static final int IntakePivotPost_HangerInterferance = 2900;
    public static final int IntakePivotPos_TopThrottle = 3250;
    public static final int IntakePivotPos_Dump = 3670;


    public static final double IntakePivotPowerDown = -.70;
    public static final double IntakePivotPowerDown_slow = -.12;
    public static final double IntakePivotPowerDown_brake = -.07;
    public static final double IntakePivotPowerUp = .85;
    public static final double IntakePivotPowerUp_slow = .35;
    public static final double IntakePivotPowerInit = -0.30;
    public static final double IntakePivotPowerUp_med = .45;

    public static final int IntakeReachPos_Tol = 70;
    public static final int IntakeReachPos_Retracted = 0;
    public static final int IntakeReachPos_ExtDump = 200;
    public static final int IntakeReachPos_ExtPickup = 400;
    public static final int IntakeReachPos_Carry = 650;
    public static final int IntakeReachPos_Extended = 1195;


    public static final double IntakeReachPowerRetract = -.9;
    public static final double IntakeReachPowerExtend = 0.9;
    public static final double IntakeReachPowerInit = -0.2;


    private static final String TAGIntakeArm = "8492-IntakeArm";
    double IntakePivotPowerCurrent = 0;
    double IntakePivotPowerDesired = 0;
    boolean cmdPivotComplete = false;
    int IntakePivotPosCurrent = IntakePivotPos_Start;
    double IntakePivotStickDeadBand = 1;


    double IntakeReachPowerCurrent = 0;
    double IntakeReachPowerDesired = 0;
    boolean cmdReachComplete = false;
    int IntakeReachPosCurrent = IntakeReachPos_Retracted;
    double IntakeReachStickDeadBand = 1;


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private Hanger hanger = null;
    private IntakePivotDestinations desiredPivotDestination = IntakePivotDestinations.Start;
    private IntakeReachDestinations desiredReachDestination = IntakeReachDestinations.Retracted;

    private IntakePivotDestinations currentPivotDestination = IntakePivotDestinations.Start;
    private IntakeReachDestinations currentReachDestination = IntakeReachDestinations.Retracted;

    //set the powers... We will need different speeds for up and down.
    private DcMotor AM1_Pivot = null;
    private DigitalChannel ArmTCH = null;

    private DcMotor AM2_Reach = null;

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
        AM1_Pivot = hardwareMap.dcMotor.get("AM1");
        AM1_Pivot.setDirection(DcMotor.Direction.REVERSE);
        AM1_Pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        AM2_Reach = hardwareMap.dcMotor.get("AM2");
        AM2_Reach.setDirection(DcMotor.Direction.FORWARD);
        AM2_Reach.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void MotorEncoderReset() {
        AM1_Pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        AM1_Pivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        AM1_Pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        AM1_Pivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        AM2_Reach.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        AM2_Reach.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void teleStart() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //  telemetry.addData("IntakeArmPower", IntakePivotPowerDesired);

        //check if under stick control [must create process (public void ...) first]
        // IntakeArmSafetyChecks();
        SetPivotMotorPower(IntakePivotPowerDesired);
        SetReachMotorPower(IntakeReachPowerDesired);
        telemetry.addData("AM2Ticks", AM2_Reach.getCurrentPosition());
        telemetry.addData("AM1Ticks", AM1_Pivot.getCurrentPosition());
    }


    private void initArmTCH() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        //runtime.startTime();

        AM1_Pivot.setPower(IntakePivotPowerInit);
        AM2_Reach.setPower(IntakeReachPowerInit);
        while (ArmTCH.getState()) {
            if (runtime.milliseconds() > 2000) {
                break;
            }
        }
        AM2_Reach.setPower(0);
        AM1_Pivot.setPower(0);
    }


    private boolean inPosition_Tol(int dest, int currPos, int tol) {
        // Tests if current position is within positional tolerance
        boolean retValue = false;
        if (Math.abs(dest - currPos) < Math.abs(tol)) {
            retValue = true;
        }
        return retValue;
    }

    public boolean atPivotDestination(IntakePivotDestinations desiredDestination) {
        // Tests if we are at desired named destination
        boolean retValue = false;

        switch (desiredDestination) {
            case Start:
                if (inPosition_Tol(IntakePivotPos_Start, IntakePivotPosCurrent, IntakePivotPos_Tol)) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Start;
                } else if (IntakePivotPosCurrent < IntakePivotPos_Start) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Start;
                }
                break;

            case Carry:
                if (inPosition_Tol(IntakePivotPos_Carry, IntakePivotPosCurrent, (int) (IntakePivotPos_Tol * 1.75))) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Carry;
                }
                break;

            case ExtPickup:
                if (inPosition_Tol(IntakePivotPos_ExtPickup, IntakePivotPosCurrent, (int) (IntakePivotPos_Tol * 1.5))) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.ExtPickup;
                }
                break;

            case Dump:
                if (inPosition_Tol(IntakePivotPos_Dump, IntakePivotPosCurrent, IntakePivotPos_Tol)) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Dump;
                } else if (IntakePivotPosCurrent > IntakePivotPos_Dump) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Dump;
                }
                break;


            case StickControl:
                // Stick control only stops at Pickup and Dump positions.

                if (inPosition_Tol(IntakePivotPos_Start, IntakePivotPosCurrent, IntakePivotPos_Tol) && IntakePivotPowerDesired < 0) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Start;

                } else if (!ArmTCH.getState()) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Start;

                } else if ((IntakePivotPosCurrent < IntakePivotPos_Start) && (IntakePivotPowerDesired < 0)) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Start;

                } else if (inPosition_Tol(IntakePivotPos_Dump, IntakePivotPosCurrent, IntakePivotPos_Tol) && IntakePivotPowerDesired > 0) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Dump;

                } else if ((IntakePivotPosCurrent > IntakePivotPos_Dump) && (IntakePivotPowerDesired > 0)) {
                    retValue = true;
                    currentPivotDestination = IntakePivotDestinations.Dump;

                }
                break;

            case Unknown:
                retValue = true;
                currentPivotDestination = IntakePivotDestinations.Unknown;
                break;

            default:
                retValue = true;
                currentPivotDestination = IntakePivotDestinations.Unknown;
                break;

        }
        cmdPivotComplete = retValue;
        return retValue;
    }


    public boolean atReachDestination(IntakeReachDestinations desiredDestination) {

        // Tests if we are at desired named destination
        boolean retValue = false;

        switch (desiredDestination) {
            case Retracted:
                if (inPosition_Tol(IntakeReachPos_Retracted, IntakeReachPosCurrent, IntakeReachPos_Tol)) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Retracted;
                } else if (IntakeReachPosCurrent < IntakeReachPos_Retracted) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Retracted;
                }
                break;

            case Carry:
                if (inPosition_Tol(IntakeReachPos_Carry, IntakeReachPosCurrent, (int) (IntakeReachPos_Tol * 1.25))) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Carry;
                }
                ;
                break;

            case ExtPickup:
                if (inPosition_Tol(IntakeReachPos_ExtPickup, IntakeReachPosCurrent, (int) (IntakeReachPos_Tol * 1.25))) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.ExtPickup;
                }
                break;

            case ExtDump:
                if (inPosition_Tol(IntakeReachPos_ExtDump, IntakeReachPosCurrent, (int) (IntakeReachPos_Tol * 1.25))) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.ExtDump;
                }
                break;

            case Extended:
                if (inPosition_Tol(IntakeReachPos_Extended, IntakeReachPosCurrent, IntakeReachPos_Tol)) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Extended;
                } else if (IntakeReachPosCurrent > IntakeReachPos_Extended) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Extended;
                }
                break;


            case StickControl:
                // Stick control only stops at Pickup and Dump positions.

                if (inPosition_Tol(IntakeReachPos_Retracted, IntakeReachPosCurrent, IntakeReachPos_Tol) && IntakeReachPowerDesired < 0) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Retracted;

                } else if ((IntakeReachPosCurrent < IntakeReachPos_Retracted) && (IntakeReachPowerDesired < 0)) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Retracted;

                } else if (inPosition_Tol(IntakeReachPos_Extended, IntakeReachPosCurrent, IntakeReachPos_Tol) && IntakeReachPowerDesired > 0) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Extended;

                } else if ((IntakeReachPosCurrent > IntakeReachPos_Extended) && (IntakeReachPowerDesired > 0)) {
                    retValue = true;
                    currentReachDestination = IntakeReachDestinations.Extended;

                }
                break;

            case Unknown:
                retValue = true;
                currentReachDestination = IntakeReachDestinations.Unknown;
                break;

            default:
                retValue = true;
                break;

        }
        cmdReachComplete = retValue;
        return retValue;
    }

    private void SetPivotMotorPower(double newMotorPower) {
        //set the motors for the intake Arm to the new power only after
        // Safety checks to prevent too low or too high
        IntakePivotPosCurrent = AM1_Pivot.getCurrentPosition();

        double newPower = newMotorPower;

        if (atPivotDestination(desiredPivotDestination)) {
            newPower = 0;
        }

        // slow down if near dump position
        if ((newPower > 0) && (IntakePivotPosCurrent > IntakePivotPos_TopThrottle) &&
                (IntakeReachPosCurrent < 600)) {
            newPower = IntakePivotPowerUp_slow;
        }

        if ((newPower > 0) && (IntakePivotPosCurrent > IntakePivotPos_TopThrottle) &&
                (IntakeReachPosCurrent > 600)) {
            newPower = IntakePivotPowerUp_med;
        }

        //slow down if near bottom position
        if ((newPower < 0 )  && (IntakePivotPosCurrent < IntakePivotPos_BottomThrottle)) {
            newPower = IntakePivotPowerDown_slow;
        }

        if ((newPower < 0 )  && (IntakePivotPosCurrent < IntakePivotPos_BottomBrake)&&
                (IntakePivotPosCurrent > IntakePivotPos_ExtPickup)) {
            newPower = IntakePivotPowerDown_brake;
        }


        //Safety Check: stop if trying to move towards start position with reach not retracted.
        if ((newPower < 0 ) &&
            (! atReachDestination(IntakeReachDestinations.Retracted)) &&
            (IntakePivotPosCurrent < IntakePivotPos_ExtPickup )){
            newPower = 0;
        }

        //Make sure the hanger is Down  AKA no going up past TopThrottle unless the hanger is down
        // This is a safety check of the hanger and intake pivot
        if (!hanger.isRetracted() && newPower > 0 &&
            (IntakePivotPosCurrent > IntakePivotPos_TopThrottle)) {
            newPower = 0;
        }

        //only set the power to the hardware when it is being changed.
        if (newPower != IntakePivotPowerCurrent) {
            IntakePivotPowerCurrent = newPower;
            IntakePivotPowerDesired = newPower;
            AM1_Pivot.setPower(IntakePivotPowerCurrent);
        }
    }


    private void SetReachMotorPower(double newMotorPower) {
        //set the motors for the intake Arm to the new power only after
        // Safety checks to prevent too low or too high
        IntakeReachPosCurrent = AM2_Reach.getCurrentPosition();

        double newPower = newMotorPower;

        if (atReachDestination(desiredReachDestination)) {
            newPower = 0;
        }

        //If pivot is too low do not allow this to extend
        if ((newPower > 0) && (IntakePivotPosCurrent < IntakePivotPos_ExtPickup)) {
            newPower = 0;
        }


        //only set the power to the hardware when it is being changed.
        if (newPower != IntakeReachPowerCurrent) {
            IntakeReachPowerCurrent = newPower;
            AM2_Reach.setPower(IntakeReachPowerCurrent);
        }
    }


    //driver is using stick control for Intake Arm
    public void cmd_PivotStickControl(double stickPos) {

        if (Math.abs(stickPos) < Math.abs(IntakePivotStickDeadBand)) {
            // If driver releases the sticks stop the motion
            if (desiredPivotDestination == IntakePivotDestinations.StickControl) {
                IntakePivotPowerDesired = 0;
            }
            return;
        } else {
            desiredPivotDestination = IntakePivotDestinations.StickControl;

            double currPower = stickPos;

            //limit the power of the stick
            if (currPower > IntakePivotPowerUp) {
                currPower = IntakePivotPowerUp;
            }


            //limit the power of the stick
            if (currPower < IntakePivotPowerDown) {
                currPower = IntakePivotPowerDown;
            }

            IntakePivotPowerDesired = currPower;
        }
    }

    //driver is using stick control for Intake Arm
    public void cmd_ReachStickControl(double stickPos) {

        if (Math.abs(stickPos) < Math.abs(IntakeReachStickDeadBand)) {
            // If driver releases the sticks stop the motion
            if (desiredReachDestination == IntakeReachDestinations.StickControl) {
                IntakeReachPowerDesired = 0;
            }

            return;
        } else {
            desiredReachDestination = IntakeReachDestinations.StickControl;
            double currPower = stickPos;

            //limit the power of the stick
            if (currPower > IntakeReachPowerExtend) {
                currPower = IntakeReachPowerExtend;
            }

            //limit the power of the stick
            if (currPower < IntakeReachPowerRetract) {
                currPower = IntakeReachPowerRetract;
            }

            IntakeReachPowerDesired = currPower;
        }
    }

    public void cmd_movePivotToStartPos() {

        desiredPivotDestination = IntakePivotDestinations.Start;
        if (atPivotDestination(desiredPivotDestination)) {
            IntakePivotPowerDesired = 0;
        } else {
            //Start can only be reached if the reach is fully retracted.
            cmd_moveReachToRetractredPos();
            IntakePivotPowerDesired = IntakePivotPowerDown;
        }

    }

    public void cmd_movePivotToDumpPos() {
        desiredPivotDestination = IntakePivotDestinations.Dump;
        if (atPivotDestination(desiredPivotDestination)) {
            IntakePivotPowerDesired = 0;
        } else {
            IntakePivotPowerDesired = IntakePivotPowerUp;
        }

    }

    public void cmd_movePivotToCarryPos() {
        desiredPivotDestination = IntakePivotDestinations.Carry;
        if (atPivotDestination(desiredPivotDestination)) {
            //we are at carry Pos   Stop Now
            IntakePivotPowerDesired = 0;
        } else if (IntakePivotPosCurrent > IntakePivotPos_Carry) {
            IntakePivotPowerDesired = IntakePivotPowerDown;
        } else if (IntakePivotPosCurrent < IntakePivotPos_Carry) {
            IntakePivotPowerDesired = IntakePivotPowerUp;
        }

    }

    public void cmd_movePivotToExtPickupPos() {
        desiredPivotDestination = IntakePivotDestinations.Start;
        if (atPivotDestination(desiredPivotDestination)) {
            //we are at carry Pos   Stop Now
            IntakePivotPowerDesired = 0;
        } else if (IntakePivotPosCurrent > IntakePivotPos_Start) {
            IntakePivotPowerDesired = IntakePivotPowerDown;
        } else if (IntakePivotPosCurrent < IntakePivotPos_Carry) {
            IntakePivotPowerDesired = IntakePivotPowerUp;
        }

    }


    public void cmd_moveReachToRetractredPos() {
        desiredReachDestination = IntakeReachDestinations.Retracted;
        if (atReachDestination(desiredReachDestination)) {
            IntakeReachPowerDesired = 0;
        } else {
            IntakeReachPowerDesired = IntakeReachPowerRetract;
        }
    }

    public void cmd_moveReachToExtendedPos() {
        desiredReachDestination = IntakeReachDestinations.Extended;
        if (atReachDestination(desiredReachDestination)) {
            IntakeReachPowerDesired = 0;
        } else {
            IntakeReachPowerDesired = IntakeReachPowerExtend;
        }
    }


    public void cmd_moveReachToExtDumpPos() {
        desiredReachDestination = IntakeReachDestinations.ExtDump;
        if (atReachDestination(desiredReachDestination)) {
            IntakeReachPowerDesired = 0;
        } else {
            if (IntakeReachPosCurrent < IntakeReachPos_ExtDump) {
                IntakeReachPowerDesired = IntakeReachPowerExtend;
            }
            else {
                IntakeReachPowerDesired = IntakeReachPowerRetract;
            }
        }
    }


    public void cmd_moveReachToCarryPos() {
        desiredReachDestination = IntakeReachDestinations.Carry;
        if (atReachDestination(desiredReachDestination)) {
            //we are at carry Pos   Stop Now
            IntakeReachPowerDesired = 0;
        } else if (IntakeReachPosCurrent > IntakeReachPos_Carry) {
            IntakeReachPowerDesired = IntakeReachPowerRetract;
        } else if (IntakeReachPosCurrent < IntakeReachPos_Carry) {
            IntakeReachPowerDesired = IntakeReachPowerExtend;
        }

    }

    public void cmd_moveToExtPickup() {
        cmd_movePivotToExtPickupPos();
        cmd_moveReachToExtendedPos();
    }

    public void cmd_moveToExtDump() {
        cmd_movePivotToDumpPos();
        cmd_moveReachToExtDumpPos();
    }

    public void setHanger(Hanger hangR) {
        hanger = hangR;
    }

    public int getPivotPOS_Ticks() {
        return IntakePivotPosCurrent;
    }

    public int getReachPOS_Ticks() {
        return IntakeReachPosCurrent;
    }


    public boolean isPivotAtStart() {
        return currentPivotDestination == IntakePivotDestinations.Start;

    }

    public boolean isPivotAtDump() {
        return currentPivotDestination == IntakePivotDestinations.Dump;
    }

    public boolean isPivotAtCarry() {
        return currentPivotDestination == IntakePivotDestinations.Carry;
    }

    public boolean isReachRetracted() {
        return currentReachDestination == IntakeReachDestinations.Retracted;
    }

    public boolean isReachExtended() {
        return currentReachDestination == IntakeReachDestinations.Extended;
    }

    public boolean isReachCarry() {
        return currentReachDestination == IntakeReachDestinations.Carry;
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        AM1_Pivot.setPower(0.0);
        AM2_Reach.setPower(0.0);
    }


    public static enum IntakePivotDestinations {
        Start,
        Carry,
        Dump,
        ExtPickup,
        StickControl,
        Unknown
    }

    public static enum IntakeReachDestinations {
        Retracted,
        Extended,
        Carry,
        ExtPickup,
        ExtDump,
        StickControl,
        Unknown
    }
}