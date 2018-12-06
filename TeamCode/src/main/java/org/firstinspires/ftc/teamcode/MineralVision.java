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
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tf lite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private int loopCounter = 0;
    private boolean visionComplete = true;
    private int visionTimeout = 400000;
    private ElapsedTime runtime = new ElapsedTime();

    public static enum GOLD_LOCATION {
        UNKNOWN,
        LEFT,
        CENTER,
        RIGHT
    }

    private GOLD_LOCATION gold_location = GOLD_LOCATION.UNKNOWN;

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "ASrOKuv/////AAABmU6x4I/irUd9rSBiByWNEU9W5d6LMOF4HcVnDpFdolVX1uvuwxLkcVNxmvVWSG7X1yYHbgYzM1/NF+9rvaMWi+0rc1Pn+eAi324EMHiQmIqmybTW3VvwkoLXdnOS/LERjn+Ax/3BiRkp7fL3LEQj7nDWZ0PMtFJqrITCH9/r6M9d5gloZdcPnAo2h+Qp1onchXpXjXCrP3Ud+wbO2RyH1YG1Tv2khrt1kqShu7H0cCP4ZCHSM3oFl5quTOpgVYjohjqlKelmI8du2y5OnecKWhYQoTLeHRjesYcSyY0vSs2LpejTasCNpsrJXSlNJdMcDmHnTq3I84GBI0PUMWdwpCCY8fbX5MlUekIRx4QWXFyj";
    /*
    Backup Keys... Created Dec 6, 2018 after the first key stopped working.
    private static final String VUFORIA_KEY = "AZlFLnv/////AAABmY/YFf6hlEdtqHlnYm3nQVgcLbHr4D6XJOMIseGl2EGCF1s3+5yJB2qNy11qZduxRuYI3im4x2xmuP9I3IrpFNyckA3W3s7JkVc5CG/3Q4PTPhqlct2ag1crWlBuHF3C6kevaw4tDYxnCfe936HyYLKiyhSA3PiDfI0fMrullY3Mvm31s/lNuarzbn6Nu+FFqsR3fUKMrC+nTyk4nOB2b3RekiExeN58UCLF8H7QJJUWBSYDtuULn+PwerG4g+2OU++2QZyVjFjBQFj/yMkH6nzYq33Kso0gakcfdhpLmE6Tz/D1lF9/WOmcUK3XfgeOt0BU0NaSvhEetyUh4XgIx+Cag0LokS5AefGWgVHolpsN";
    private static final String VUFORIA_KEY = "AcIEAsf/////AAABmbig28bwVUlbjo4tZsTf7ihpDyKgTCtJiMRQ2QHcjKnVlu9N/xrQcy9anGWYu6Q71hkZgZbjAWztlxRzEMWR6kVbLJ6L1tOYQyw50WKen5Y10a+IPR7Nht7vLTHtjtxXLJg1nm6+o9wHdiWBNYtEoAq6FiH1F34KuyHacUkxQ5vQZhXMEpds+4dIfPm3/J/1RxSHXcC+M5qvS/mzyTsu8UwAfHCP2zSoI3YgRuTwcu4HSy9YABvgGZl82gQ7dvuyL6w+6fhSkH6TxgBOn+OOjT60VtJYicVc1mf95gx2rsaylVKL8gtt82cGggYemlgVIYNmSNdBKVki1ZarVcRFLTFzwEx/xLdC7F97V86SkhBV";
    */


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

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
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
    }

    public boolean getVisionComplete() {
        return visionComplete;
    }


    private void loopLogic() {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("Objects Detected", updatedRecognitions.size());
                RobotLog.aa(TAGMineralVision, "Loop:" + loopCounter +
                        " Objects Detected:" + updatedRecognitions.size());
                loopCounter = loopCounter + 1;
                if (updatedRecognitions.size() >= 3 && updatedRecognitions.size() < 6) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    int silverMineral2X = -1;
                    int goldMineralY = -1;
                    for (Recognition recognition : updatedRecognitions) {

                        RobotLog.aa(TAGMineralVision, recognition.getLabel() +
                                ": " + recognition.getLeft() + ": " + recognition.getTop() +
                                ": " + recognition.getConfidence() +
                                ": " + recognition.estimateAngleToObject(AngleUnit.DEGREES));

                        if (recognition.getTop() > 0) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMineralX = (int) recognition.getLeft();
                                goldMineralY = (int) recognition.getTop();
                            } else if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();
                            } else {
                                silverMineral2X = (int) recognition.getLeft();
                            }
                        }
                    }

                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Left:" + goldMineralX + ":" + goldMineralY);
                            RobotLog.aa(TAGMineralVision, "Gold Mineral Position Left:" + goldMineralX + ":" + goldMineralY);
                            gold_location = GOLD_LOCATION.LEFT;

                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Right:" + goldMineralX + ":" + goldMineralY);
                            RobotLog.aa(TAGMineralVision, "Gold Mineral Position Right:" + goldMineralX + ":" + goldMineralY);
                            gold_location = GOLD_LOCATION.RIGHT;

                        } else {
                            telemetry.addData("Gold Mineral Position", "Center:" + goldMineralX + ":" + goldMineralY);
                            RobotLog.aa(TAGMineralVision, "Gold Mineral Position Center:" + goldMineralX + ":" + goldMineralY);
                            gold_location = GOLD_LOCATION.CENTER;

                        }
                        visionComplete = true;
                    }
                } else {
                    gold_location = GOLD_LOCATION.UNKNOWN;
                }
                telemetry.update();
            } else {
                telemetry.addData("Objects Detected", 0);
            }

            if (runtime.milliseconds() > visionTimeout) {
                visionComplete = true;
            }
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