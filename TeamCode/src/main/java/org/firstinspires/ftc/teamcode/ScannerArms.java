package org.firstinspires.ftc.teamcode;

/* controls all actions the intake arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class ScannerArms extends OpMode {
    private static final String TAGIntakeArm = "8492-IntakeArm";
    // sometimes it helps to multiply the raw RGB values with a scale factor
    // to amplify/attentuate the measured values.
    final double SCALE_FACTOR = 255;
    boolean cmdComplete = false;
    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValuesGold[] = {0F, 0F, 0F};
    float hsvValuesSilver[] = {0F, 0F, 0F};
    float hsvTol[] = {5, 5, 5};
    float hsvValuesRight[] = hsvValuesGold;
    float hsvValuesLeft[] = hsvValuesGold;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private SCANNER_ARM_STATES ScannerArmState_desired;
    private SCANNER_ARM_STATES ScannerArmState_current;
    private ElapsedTime ScannerArmTimer = null;
    private int ScannerArmMoveTime = 1500;
    // Define the servos.
    private Servo scanSvoR = null;
    private Servo scanSvoL = null;
    private double scanSvoRPos_up = 0;
    private double scanSvoRPos_down = .7;
    private double scanSvoLPos_up = .7;
    private double scanSvoLPos_down = 0;
    private ColorSensor colorR = null;
    private ColorSensor colorL = null;
    private DistanceSensor distanceR = null;
    private DistanceSensor distanceL = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        telemetry.addData("ScannerArms", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        scanSvoR = hardwareMap.servo.get("ScanSvoR");
        scanSvoL = hardwareMap.servo.get("ScanSvoL");

        // get a reference to the color sensor.
        colorR = hardwareMap.get(ColorSensor.class, "ColorR");
        colorL = hardwareMap.get(ColorSensor.class, "ColorL");

        distanceR = hardwareMap.get(DistanceSensor.class, "DistR");
        distanceL = hardwareMap.get(DistanceSensor.class, "DistL");

        ScannerArmTimer = new ElapsedTime();
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
        scanSvoL.setPosition(scanSvoLPos_up);
        scanSvoR.setPosition(scanSvoRPos_up);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        switch (ScannerArmState_desired) {
            case SCANNER_ARM_STATES_Moving_Down: {
                ScannerArmState_current = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Down;
                ScannerArmState_desired = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Down;
                ScannerArmTimer.reset();
                scanSvoL.setPosition(scanSvoLPos_down);
                scanSvoR.setPosition(scanSvoRPos_down);
                break;
            }

            case SCANNER_ARM_STATES_Moving_Up: {
                ScannerArmState_current = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Up;
                ScannerArmState_desired = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Up;
                ScannerArmTimer.reset();
                scanSvoL.setPosition(scanSvoLPos_up);
                scanSvoR.setPosition(scanSvoRPos_up);
                break;
            }

            case SCANNER_ARM_STATES_Up: {
                if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                    ScannerArmState_current = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Up;
                    ScannerArmTimer.reset();
                }
                break;
            }
            case SCANNER_ARM_STATES_Down: {
                if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                    ScannerArmState_current = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Down;
                    ScannerArmTimer.reset();
                }
                break;
            }

        }

    }

    public boolean atDestination(SCANNER_ARM_STATES test_state) {
        return (ScannerArmState_current == test_state);
    }

    private boolean isGold(float hsvValues[]) {
        // return true if the values represent gold

        boolean retValue = true;
        for (int i = 0; i <= 2; i++) {
            retValue = inTol(hsvValues[i], hsvValuesGold[i], hsvTol[i]) && retValue;
        }

        return retValue;
    }

    private boolean inTol(float sensorValue, float knownValue, float tol) {

        return (Math.abs(sensorValue - knownValue) < Math.abs(tol));

    }

    private boolean isSilver(float hsvValues[]) {
        // return true if the values represent silver
        boolean retValue = true;

        for (int i = 0; i <= 2; i++) {
            retValue = inTol(hsvValues[i], hsvValuesSilver[i], hsvTol[i]) && retValue;
        }

        return retValue;
    }

    public void cmd_moveDown() {
        ScannerArmState_desired = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Down;


    }

    public void cmd_moveUp() {
        ScannerArmState_desired = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Up;
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }


    public static enum SCANNER_ARM_STATES {
        SCANNER_ARM_STATES_Up,
        SCANNER_ARM_STATES_Moving_Down,
        SCANNER_ARM_STATES_Down,
        SCANNER_ARM_STATES_Moving_Up
    }
}