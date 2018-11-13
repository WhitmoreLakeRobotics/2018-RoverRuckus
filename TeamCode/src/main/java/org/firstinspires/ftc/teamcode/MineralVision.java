package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

public class MineralVision extends BaseHardware {

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

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
    public void init(){

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
     *
     */
    public void start() {
        startLogic();
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    public void loop(){
        loopLogic ();
    }

    /**
     * User defined stop method
     * <p>
     * This method will be called when this op mode is first disabled
     *
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

    private void loopLogic () {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() >= 3 && updatedRecognitions.size() < 6) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    int silverMineral2X = -1;
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getTop() > 300) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMineralX = (int) recognition.getLeft();
                            } else if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();
                            } else {
                                silverMineral2X = (int) recognition.getLeft();
                            }
                        }
                    }
                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Left");
                            gold_location=GOLD_LOCATION.LEFT;
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Right");
                            gold_location=GOLD_LOCATION.RIGHT;
                        } else {
                            telemetry.addData("Gold Mineral Position", "Center");
                            gold_location=GOLD_LOCATION.CENTER;
                        }
                    }
                }
                else {
                    gold_location=GOLD_LOCATION.UNKNOWN;
                }
                telemetry.update();
            }
        }

    }

    private void startLogic () {
        if (tfod != null) {
            tfod.activate();
        }
    }

    private void stopLogic () {
        if (tfod != null) {
            tfod.shutdown();
        }
    }

    public GOLD_LOCATION getGoldLocation (){
        return gold_location;
    }

    public boolean isGoldLeft (){
        return (gold_location == GOLD_LOCATION.LEFT);
    }

    public boolean isGoldRight (){
        return (gold_location == GOLD_LOCATION.RIGHT);
    }

    public boolean isGoldCenter (){
        return (gold_location == GOLD_LOCATION.CENTER);
    }

}