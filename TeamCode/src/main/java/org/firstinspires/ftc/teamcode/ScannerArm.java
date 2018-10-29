package org.firstinspires.ftc.teamcode;

/* controls all actions the intake arm

 */


import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class ScannerArm extends OpMode {

    private static final String TAGIntakeArm = "8492-ScannerArm";

    /* Declare OpMode members. */

    // sometimes it helps to multiply the raw RGB values with a scale factor
    // to amplify/attentuate the measured values.
    final double SCALE_FACTOR = 255;

    boolean cmdComplete = false;

    // hsvValues is an array that will hold the hue, saturation, and value information.
    public static float hsvValuesGold[] = {0F, 0F, 0F};
    public static float hsvValuesSilver[] = {0F, 0F, 0F};

    public static float hsvValuesZero [] = {0,0,0};
    float hsvTol[] = {5, 5, 5};
    float hsvSensorValues[] = hsvValuesGold;

    double dist2Sensor = 0;
    private ElapsedTime runtime = new ElapsedTime();


    private SCANNER_ARM_STATES ScannerArmState_desired;
    private SCANNER_ARM_STATES ScannerArmState_current;

    private ElapsedTime ScannerArmTimer = null;
    private int ScannerArmMoveTime = 1500;

    // Define the hardware
    private Servo scanSvo = null;
    private ColorSensor colorSensor = null;
    private DistanceSensor distanceSensor = null;


    private double scanSvoPos_up = 0;
    private double scanSvoPos_down = 0;


    public void setServo (Servo svro){
        scanSvo = svro;
    }

    public void setPositions(double upPos, double downPos){
        scanSvoPos_up = upPos;
        scanSvoPos_down = downPos;
    }

    public void setDistanceSensor(DistanceSensor ds){
        distanceSensor = ds;
    }

    public void setColorSensor(ColorSensor cs){
        colorSensor = cs;
    }
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

    telemetry.addData("ScannerArms", "Initialized");
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
        scanSvo.setPosition(scanSvoPos_up);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        if (ScannerArmState_current != ScannerArmState_desired) {

            switch (ScannerArmState_desired) {
                case SCANNER_ARM_STATES_Moving_Down: {
                    ScannerArmState_current = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Down;
                    ScannerArmTimer.reset();
                    scanSvo.setPosition(scanSvoPos_down);
                    break;
                }

                case SCANNER_ARM_STATES_Moving_Up: {
                    ScannerArmState_current = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Up;
                    dist2Sensor = 1000;
                    hsvSensorValues = hsvValuesZero;
                    ScannerArmTimer.reset();
                    scanSvo.setPosition(scanSvoPos_up);
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

        // Only scan for color when they are down....
        if (ScannerArmState_current == SCANNER_ARM_STATES.SCANNER_ARM_STATES_Down) {
            Color.RGBToHSV((int) (colorSensor.red() * SCALE_FACTOR),
                    (int) (colorSensor.green() * SCALE_FACTOR),
                    (int) (colorSensor.blue() * SCALE_FACTOR),
                    hsvSensorValues);

            dist2Sensor = distanceSensor.getDistance(DistanceUnit.INCH);
        }
    }

    public boolean atDestination(SCANNER_ARM_STATES test_state) {
        return (ScannerArmState_current == test_state);
    }


    public boolean getIsGold() {
        // return true if the values represent gold

        boolean retValue = true;
        for (int i = 0; i <= 2; i++) {
            retValue = inTol(hsvSensorValues[i], hsvValuesGold[i], hsvTol[i]) && retValue;
        }

        return retValue;
    }

    private boolean inTol(float sensorValue, float knownValue, float tol) {

        return (Math.abs(sensorValue - knownValue) < Math.abs(tol));

    }

    public boolean getIsSilver() {
        // return true if the values represent silver
        boolean retValue = true;

        for (int i = 0; i <= 2; i++) {
            retValue = inTol(hsvSensorValues[i], hsvValuesSilver[i], hsvTol[i]) && retValue;
        }

        return retValue;
    }

    public void cmd_moveDown() {
        ScannerArmState_desired = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Down;
    }


    public void cmd_moveUp() {
        ScannerArmState_desired = SCANNER_ARM_STATES.SCANNER_ARM_STATES_Moving_Up;
    }


    public boolean getIsDown() {
        return ScannerArmState_current == SCANNER_ARM_STATES.SCANNER_ARM_STATES_Down;
    }

    public boolean getIsUp() {
        return ScannerArmState_current == SCANNER_ARM_STATES.SCANNER_ARM_STATES_Up;
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