package org.firstinspires.ftc.teamcode;

/* controls the combined  actions the Scanner Arms

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "ScannerArms", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class ScannerArms extends OpMode {
    private static final String TAGIntakeArm = "8492-ScannerArms";
    private ScannerArm scannerArmRight = new ScannerArm();
    private ScannerArm scannerArmLeft = new ScannerArm();


    private double scanSvoRPos_up = 0;
    private double scanSvoRPos_down = .7;
    private double scanSvoLPos_up = .7;
    private double scanSvoLPos_down = 0;

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
        scannerArmLeft.setServo(hardwareMap.servo.get("ScanSvoR"));
        scannerArmLeft.setColorSensor(hardwareMap.get(ColorSensor.class, "ColorL"));
        scannerArmLeft.setDistanceSensor(hardwareMap.get(DistanceSensor.class, "DistL"));
        scannerArmLeft.setPositions(scanSvoLPos_up, scanSvoLPos_down);
        scannerArmLeft.init();

        scannerArmRight.setServo(hardwareMap.servo.get("ScanSvoL"));
        scannerArmRight.setColorSensor(hardwareMap.get(ColorSensor.class, "ColorR"));
        scannerArmRight.setDistanceSensor(hardwareMap.get(DistanceSensor.class, "DistR"));
        scannerArmRight.setPositions(scanSvoRPos_up, scanSvoRPos_down);
        scannerArmRight.init();

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        scannerArmLeft.init_loop();
        scannerArmRight.init_loop();
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        scannerArmLeft.start();
        scannerArmRight.start();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        scannerArmRight.loop();
        scannerArmLeft.loop();
    }

    public void cmdMoveDown(){
        scannerArmRight.cmd_moveDown();
        scannerArmLeft.cmd_moveDown();
    }

    public void cmdMoveUp() {
        scannerArmRight.cmd_moveUp();
        scannerArmLeft.cmd_moveUp();
    }

    public void cmdMoveUpLeft() {
        scannerArmLeft.cmd_moveUp();
    }

    public void cmdMoveUpRight() {
        scannerArmRight.cmd_moveUp();
    }

    public boolean getIsDown(){
        return (scannerArmLeft.getIsDown() && scannerArmRight.getIsDown());
    }


    public boolean getIsUp() {
        return (scannerArmLeft.getIsUp() && scannerArmRight.getIsUp());
    }

    public boolean getIsUpLeft() {
        return (scannerArmLeft.getIsUp());
    }

    public boolean getIsUpRight() {
        return (scannerArmRight.getIsUp());
    }


    public boolean isGoldLeft() {
        return (scannerArmLeft.getIsGold());

    }

    public boolean isGoldCenter() {
        return (scannerArmLeft.getIsSilver() && scannerArmRight.getIsSilver());

    }

    public boolean isGoldRight() {
        return (scannerArmLeft.getIsGold());

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        scannerArmRight.stop();
        scannerArmLeft.stop();
    }

}