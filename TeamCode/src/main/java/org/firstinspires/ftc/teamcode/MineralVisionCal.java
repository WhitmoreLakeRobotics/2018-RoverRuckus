package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name = "MineralVision-Calibrate", group = "TeleOp")
//@Disabled

public class MineralVisionCal extends OpMode {

    public MineralVision mineralVision = new MineralVision();

    public void init() {
        mineralVision.hardwareMap = hardwareMap;
        mineralVision.telemetry = telemetry;
        mineralVision.init();

    }

    public void init_Loop() {
        mineralVision.init_loop();
    }

    public void start() {
        mineralVision.start();
        mineralVision.setVisionTimeout(300000);
        mineralVision.startVision();
    }

    public void loop () {
        mineralVision.loop();

        if (mineralVision.getVisionComplete()){
            mineralVision.startVision();
        }
    }

    public void stop () {
        mineralVision.stop();
    }

}
