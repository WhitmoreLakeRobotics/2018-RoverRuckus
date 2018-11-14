package org.firstinspires.ftc.teamcode;

/* controls all actions the intake arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class ScannerArm extends OpMode {

    private static final String TAGIntakeArm = "8492-ScannerArm";

    /* Declare OpMode members. */

    private ElapsedTime runtime = new ElapsedTime();


    private SCANNER_ARM_STATES ScannerArmState_desired = SCANNER_ARM_STATES.MOVING_UP;
    private SCANNER_ARM_STATES ScannerArmState_current = SCANNER_ARM_STATES.UNKNOWN;

    private ElapsedTime ScannerArmTimer = null;
    private int ScannerArmMoveTime = 1500;

    // Define the hardware
    private Servo scanSvo = null;
    private double scanSvoPos_up = 0;
    private double scanSvoPos_down = 0;


    public void setServo (Servo svro){
        scanSvo = svro;
    }

    public void setPositions(double upPos, double downPos){
        scanSvoPos_up = upPos;
        scanSvoPos_down = downPos;
    }

    @Override
    public void init() {

        telemetry.addData("ScannerArm", "Initialized");
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
                case MOVING_DOWN: {
                    ScannerArmState_current = SCANNER_ARM_STATES.MOVING_DOWN;
                    ScannerArmState_desired = SCANNER_ARM_STATES.DOWN;
                    ScannerArmTimer.reset();
                    scanSvo.setPosition(scanSvoPos_down);
                    break;
                }

                case MOVING_UP: {
                    ScannerArmState_current = SCANNER_ARM_STATES.MOVING_UP;
                    ScannerArmState_desired = SCANNER_ARM_STATES.UP;
                    ScannerArmTimer.reset();
                    scanSvo.setPosition(scanSvoPos_up);
                    break;
                }

                case UP: {
                    if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                        ScannerArmState_current = SCANNER_ARM_STATES.UP;
                    }
                    break;
                }
                case DOWN: {
                    if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                        ScannerArmState_current = SCANNER_ARM_STATES.DOWN;
                    }
                    break;
                }
            }
        }

    }

    public boolean atDestination(SCANNER_ARM_STATES test_state) {
        return (ScannerArmState_current == test_state);
    }

    public void cmd_moveDown() {
        ScannerArmState_desired = SCANNER_ARM_STATES.MOVING_DOWN;
    }


    public void cmd_moveUp() {
        ScannerArmState_desired = SCANNER_ARM_STATES.MOVING_UP;
    }


    public boolean getIsDown() {
        return ScannerArmState_current == SCANNER_ARM_STATES.DOWN;
    }

    public boolean getIsUp() {
        return ScannerArmState_current == SCANNER_ARM_STATES.UP;
    }



    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

    public static enum SCANNER_ARM_STATES {
        UP,
        MOVING_DOWN,
        DOWN,
        MOVING_UP,
        UNKNOWN
    }
}