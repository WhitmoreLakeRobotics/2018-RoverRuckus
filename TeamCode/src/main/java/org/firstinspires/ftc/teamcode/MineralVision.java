package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

public class MineralVision extends BaseHardware {

    private static final String TAGMineralVision = "8492-MineralVision";
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private int loopCounter = 0;
    private boolean visionComplete = true;
    private int visionTimeout = 2500;
    private ElapsedTime runtime = new ElapsedTime();

    public static enum GOLD_LOCATION {
        UNKNOWN,
        LEFT,
        CENTER,
        RIGHT
    }

    private GOLD_LOCATION gold_location = GOLD_LOCATION.UNKNOWN;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    public void init() {

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

    }


    /**
     * User defined init_loop method
     * <p>
     * This method will be called repeatedly when the INIT button is pressed.
     * This method is optional. By default this method takes no action.
     */
    public void init_loop() {

    }

    /**
     * User defined start method.
     * <p>
     * This method will be called once when the PLAY button is first pressed.
     * This method is optional. By default this method takes not action.
     * Example usage: Starting another thread.
     */
    public void start() {
        startLogic();
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    public void loop() {
        if (!visionComplete) {
            loopLogic();
        }
    }

    /**
     * User defined stop method
     * <p>
     * This method will be called when this op mode is first disabled
     * <p>
     * The stop method is optional. By default this method takes no action.
     */
    void stop() {
        stopLogic();
    }


    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = MineralVisionKey.VUFORIA_KEY;
        //parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {

        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public void startVision() {
        runtime.reset();
        visionComplete = false;
        gold_location = GOLD_LOCATION.UNKNOWN;
    }

    public void setVisionTimeout(int mSecVisionTimeout) {
        visionTimeout = mSecVisionTimeout;
    }

    public boolean getVisionComplete() {
        return visionComplete;
    }


    private void loopLogic() {
        int goldCount = 0;
        int silverCount = 0;

        if (tfod != null) {

            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            gold_location = GOLD_LOCATION.UNKNOWN;
            if (updatedRecognitions != null) {
                // telemetry.addData("Objects Detected", updatedRecognitions.size());
                RobotLog.aa(TAGMineralVision, "Loop:" + loopCounter +
                        " Objects Detected:" + updatedRecognitions.size());
                loopCounter = loopCounter + 1;
                //if (updatedRecognitions.size() < 6) {
                int MineralIndex = 0;
                for (Recognition recognition : updatedRecognitions) {
                    RobotLog.aa(TAGMineralVision, "Mineral " + MineralIndex +
                            " is " + recognition.getLabel() +
                            ": " + recognition.getLeft() + ": " + recognition.getTop() +
                            ": " + recognition.getConfidence() +
                            ": " + recognition.estimateAngleToObject(AngleUnit.DEGREES));

                    MineralIndex = MineralIndex + 1;
                    if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                        goldCount = goldCount + 1;
                    } else {
                        silverCount = silverCount + 1;
                    }

                    if (recognition.getTop() > 300) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            visionComplete = true;
                            telemetry.addData("Gold Mineral", "Left:" + recognition.getLeft() + "  Top:" + recognition.getTop());
                            if (recognition.getLeft() < 150) {
                                telemetry.addData("Gold Mineral on:", "LEFT");
                                gold_location = GOLD_LOCATION.LEFT;
                            } else if (recognition.getLeft() > 450) {
                                gold_location = GOLD_LOCATION.RIGHT;
                                telemetry.addData("Gold Mineral on:", "RIGHT");
                            } else {
                                gold_location = GOLD_LOCATION.CENTER;
                                telemetry.addData("Gold Mineral on:", "CENTER");
                            }
                        }
                    }
                }
            }
            //}
            telemetry.addData("GoldCount", goldCount);
            telemetry.addData("SilverCount", silverCount);
            telemetry.update();
        } else {
            telemetry.addData("Objects Detected", 0);
        }

        if (runtime.milliseconds() > visionTimeout) {
            visionComplete = true;
        }
    }


    private void startLogic() {
        if (tfod != null) {
            tfod.activate();
        }
    }

    private void stopLogic() {
        if (tfod != null) {
            tfod.shutdown();
        }
    }

    public GOLD_LOCATION getGoldLocation() {
        return gold_location;
    }

    public boolean isGoldLeft() {
        return (gold_location == GOLD_LOCATION.LEFT);
    }

    public boolean isGoldRight() {
        return (gold_location == GOLD_LOCATION.RIGHT);
    }

    public boolean isGoldCenter() {
        return (gold_location == GOLD_LOCATION.CENTER);
    }

}