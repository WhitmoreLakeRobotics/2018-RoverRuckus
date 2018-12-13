/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//package org.firstinspires.ftc.robotcontroller.external.samples;
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;


@TeleOp(name = "Teleop-Comp", group = "TeleOp")
//@Disabled
public class Teleop extends OpMode {
    private static final String TAGTeleop = "8492-Teleop";
    Chassis RBTChassis = new Chassis();
    // Declare OpMode members.
    // boolean gamepad2_a_pressed = false;
    // boolean gamepad2_b_pressed = false;
    // boolean gamepad2_x_pressed = false;
    // boolean gamepad2_y_pressed = false;

    // private double RightMotorPower = 0;


    private double powerNormal = .6;


    private double powerMax = 8;



    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("TeleOp", "Initialized");
        RBTChassis.setParentMode(Chassis.PARENTMODE.PARENT_MODE_TELE);
        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.setMaxPower(powerNormal);
        RBTChassis.init();

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        RBTChassis.init_loop();


    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        Runtime.getRuntime();
        RBTChassis.start();
        RBTChassis.setMotorMode_RUN_WITHOUT_ENCODER();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        RBTChassis.loop();

        RBTChassis.doTeleop(joystickMath(-gamepad1.left_stick_y), joystickMath(-gamepad1.right_stick_y));

        //telemetry.addData("HangerPos", RBTChassis.hanger.getHangerPos());
        //telemetry.addData("IntakeArmPos", RBTChassis.intakeArm.IntakePivotPosCurrent());

        //RBTChassis.hanger.cmdStickControl(joystickMath(gamepad2.right_stick_y));
        RBTChassis.intakeArm.cmd_PivotStickControl(joystickMath(-gamepad2.left_stick_y));
        RBTChassis.intakeArm.cmd_ReachStickControl(joystickMath(-gamepad2.right_stick_y));

        if (gamepad2.dpad_down){
            RBTChassis.scannerArms.cmdMoveDownLeft();
        }

        if (gamepad2.dpad_up){
            RBTChassis.scannerArms.cmdMoveUpLeft();
        }

        if (gamepad2.dpad_left){
            RBTChassis.scannerArms.cmdMoveDownRight();
        }

        if (gamepad2.dpad_right){
            RBTChassis.scannerArms.cmdMoveUpRight();
        }


        if (gamepad2.a) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_RETRACTED);
            RobotLog.aa(TAGTeleop, "gamepad2 a pressed ");
            // gamepad2_a_pressed = true;
            // gamepad2_b_pressed = false;
            // gamepad2_x_pressed = false;
            // gamepad2_y_pressed = false;

        }


        if (gamepad2.b) {
            RBTChassis.hanger.cmd_MoveToTarget(Hanger.HANGERPOS_EXNTENDED);
            RobotLog.aa(TAGTeleop, "gamepad2 b pressed ");
            // gamepad2_a_pressed = false;
            // gamepad2_b_pressed = true;
            // gamepad2_x_pressed = false;
            // gamepad2_y_pressed = false;
        }


        if (gamepad2.x) {
            RBTChassis.intakeArm.cmd_moveToExtPickup();
            RobotLog.aa(TAGTeleop, "gamepad2 x pressed ");
            // gamepad2_a_pressed = false;
            // gamepad2_b_pressed = false;
            // gamepad2_x_pressed = true;
            // gamepad2_y_pressed = false;
        }

        if (gamepad2.y) {
            RBTChassis.intakeArm.cmd_movePivotToDumpPos();
            RobotLog.aa(TAGTeleop, "gamepad2 y pressed ");
            // gamepad2_a_pressed = false;
            // gamepad2_b_pressed = false;
            // gamepad2_x_pressed = false;
            // gamepad2_y_pressed = true;
        }


        if (gamepad2.left_bumper) {
            RBTChassis.intakeArm.cmd_moveReachToExtendedPos();
            RobotLog.aa(TAGTeleop, "gamepad2 left bumper pressed ");
        }

        if (gamepad2.right_bumper) {
            RBTChassis.intakeArm.cmd_moveReachToRetractredPos();
            RobotLog.aa(TAGTeleop, "gamepad2 right bumper pressed ");
        }


        if (gamepad1.a) {
            RobotLog.aa(TAGTeleop, "gamepad1 a pressed ");
            RBTChassis.dumpBox.cmd_ServosIn();
        }

        if (gamepad1.b) {
            RobotLog.aa(TAGTeleop, "gamepad1 b pressed ");
            RBTChassis.dumpBox.cmd_ServosOff();
        }

        if (gamepad1.y) {
            RobotLog.aa(TAGTeleop, "gamepad1 y pressed ");
            RBTChassis.dumpBox.cmd_ServosOut();
        }


        if (gamepad1.left_bumper) {
            RBTChassis.setMaxPower(powerMax);
        }

        if (gamepad1.right_bumper){
            RBTChassis.setMaxPower(powerNormal);
        }

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        RBTChassis.stop();
    }

    public double joystickMath(double joyValue) {
        int sign = 1;
        double retValue = 0;
        if (joyValue < 0) {
            sign = -1;
        }
        return Math.abs(Math.pow(joyValue, 2)) * sign;
    }

}
