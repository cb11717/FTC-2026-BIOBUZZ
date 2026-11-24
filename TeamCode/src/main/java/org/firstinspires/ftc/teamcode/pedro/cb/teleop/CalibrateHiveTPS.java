package org.firstinspires.ftc.teamcode.pedro.cb.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.HiveShooterDistanceMap;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.ShooterPIDF;

@TeleOp(name = "CALIBRATE: Hive Distance vs TPS", group = "Calibration")
public class CalibrateHiveTPS extends OpMode {

    private static final int PIPELINE_RED_CONSOLIDATED = 4;
    private static final int PIPELINE_BLUE_CONSOLIDATED = 5;

    private static final double MAX_SHOOTER_TPS = 2800.0;
    private static final double TPS_STEP = 50.0;

    private double targetHeading = 0.0;
    private boolean wasTurning = false;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final LimelightClusterTracker clusterTracker = new LimelightClusterTracker();
    private final HiveShooterDistanceMap distanceMap = new HiveShooterDistanceMap();
    private Follower follower;

    private AndroidSoundPool androidSoundPool;
    private Servo Plow;
    private Servo PlowL;
    private CRServo PllnKick;
    private CRServo PllnKick2;
    private CRServo NectKick;
    private DcMotor Intake;
    private VoltageSensor ControlHub_VoltageSensor;
    private VoltageSensor ExpansionHub2_VoltageSensor;

    private boolean leftstickStrafe = false;
    private boolean prevStickTogglePressed = false;

    private boolean prevRecordPointPressed = false;
    private boolean prevClearMapPressed = false;

    private boolean prevDpadUp = false;
    private boolean prevDpadDown = false;
    private boolean prevDpadLeft = false;
    private boolean prevDpadRight = false;
    private boolean controllerCheckDone = false;

    // 0 = Red (Pipeline 4, tags 30-37), 1 = Blue (Pipeline 5, tags 38-45)
    private int allianceIndex = 0;
    private String lastRecordedCodeLine = "None yet";

    // Baseline starting velocities for manual calibration
    private double pollTargetTps = 1800.0;
    private double nectTargetTps = 2150.0;

    private final ElapsedTime loopTimer = new ElapsedTime();
    private double lastLoopMs = 0.0;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

        Pose initialPose = poseFactory.of(0, 0, 0);
        follower.setPose(initialPose);
        follower.update();

        ShooterPIDF.init(hardwareMap);

        Plow = hardwareMap.get(Servo.class, "Plow");
        PlowL = hardwareMap.get(Servo.class, "PlowL");
        PllnKick = hardwareMap.get(CRServo.class, "PllnKick");
        PllnKick2 = hardwareMap.get(CRServo.class, "PllnKick2");
        NectKick = hardwareMap.get(CRServo.class, "NectKick");
        Intake = hardwareMap.get(DcMotor.class, "Intake");

        ControlHub_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        ExpansionHub2_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Expansion Hub 2");

        allianceIndex = 0; // Default to Red (Pipeline 4)
        clusterTracker.init(
                hardwareMap,
                getPipelineForAlliance(allianceIndex),
                getIdsForAlliance(allianceIndex),
                telemetry
        );

        androidSoundPool = new AndroidSoundPool();
        androidSoundPool.initialize(SoundPlayer.getInstance());

        PllnKick.setDirection(CRServo.Direction.REVERSE);
        PllnKick2.setDirection(CRServo.Direction.REVERSE);
        PllnKick.setPower(0.0);
        PllnKick2.setPower(0.0);
        NectKick.setPower(0.0);

        Plow.setPosition(0.0);
        PlowL.setPosition(0.05);
    }

    private void applyAllianceSelection(int newIndex) {
        allianceIndex = (newIndex == 1) ? 1 : 0;
        clusterTracker.setCluster(
                getPipelineForAlliance(allianceIndex),
                getIdsForAlliance(allianceIndex)
        );
    }

    private int getPipelineForAlliance(int index) {
        return (index == 0) ? PIPELINE_RED_CONSOLIDATED : PIPELINE_BLUE_CONSOLIDATED;
    }

    private int[] getIdsForAlliance(int index) {
        return (index == 0) ? LimelightClusterTracker.RED_IDS : LimelightClusterTracker.BLUE_IDS;
    }

    private String getAllianceLabel(int index) {
        return (index == 0) ? "P4 - RED (30-37)" : "P5 - BLUE (38-45)";
    }

    private static double deadzone(double value, double dz) {
        return Math.abs(value) < dz ? 0.0 : value;
    }

    private static double normalizeAngle(double angleRad) {
        return Math.atan2(Math.sin(angleRad), Math.cos(angleRad));
    }

    @Override
    public void init_loop() {
        follower.update();

        if (gamepad2.b || gamepad2.circle) {
            applyAllianceSelection(1); // Blue (Pipeline 5)
        } else if (gamepad2.x || gamepad2.square) {
            applyAllianceSelection(0); // Red (Pipeline 4)
        }

        if (!controllerCheckDone) {
            if (gamepad1.atRest() && gamepad2.atRest()) {
                androidSoundPool.play("RawRes:ss_wookie");
            } else {
                telemetry.addLine("Controller malfunction: check gamepads are at rest.");
            }
            controllerCheckDone = true;
        }

        telemetry.addLine("Select Alliance before Start: g2.x/square = RED (P4), g2.b/circle = BLUE (P5)");
        telemetry.addData("Selected Alliance", getAllianceLabel(allianceIndex));
        telemetry.update();
    }

    @Override
    public void start() {
        follower.update();
        targetHeading = follower.pose().heading();
        wasTurning = false;

        loopTimer.reset();
        telemetry.speak("Calibration Mode Ready", null, null);
    }

    @Override
    public void loop() {
        double nowMs = loopTimer.milliseconds();
        lastLoopMs = nowMs;
        loopTimer.reset();

        if (ControlHub_VoltageSensor != null && ControlHub_VoltageSensor.getVoltage() < 9.0) {
            androidSoundPool.play("RawRes:ss_alarm");
            telemetry.speak("Battery Voltage Low", null, null);
        }

        // Toggle stick strafe mapping on rising edge of pressing both sticks
        boolean stickTogglePressed = gamepad1.left_stick_button && gamepad1.right_stick_button;
        if (stickTogglePressed && !prevStickTogglePressed) {
            leftstickStrafe = !leftstickStrafe;
        }
        prevStickTogglePressed = stickTogglePressed;

        // Clear all recorded map entries on gamepad2.touchpad
        boolean clearMapPressed = gamepad2.touchpad;
        if (clearMapPressed && !prevClearMapPressed) {
            distanceMap.clearPoints();
            distanceMap.resetTrims();
            lastRecordedCodeLine = "Map cleared";
        }
        prevClearMapPressed = clearMapPressed;

        // --- Drivetrain with Deadzones & Heading Hold ---
        double forward = deadzone(-gamepad1.left_stick_y, 0.05);
        double strafe;
        double rawTurn;
        double turnScale = 1.0 - (Math.abs(forward) / 2.0);

        if (!leftstickStrafe) {
            strafe = deadzone(gamepad1.left_stick_x, 0.05);
            rawTurn = deadzone(gamepad1.right_stick_x, 0.05) * turnScale;
        } else {
            strafe = deadzone(gamepad1.right_stick_x, 0.05);
            rawTurn = deadzone(gamepad1.left_stick_x, 0.05) * turnScale;
        }

        double currentHeading = follower.pose().heading();
        double turn;

        if (rawTurn != 0.0) {
            wasTurning = true;
            turn = rawTurn;
        } else {
            if (wasTurning) {
                targetHeading = currentHeading;
                wasTurning = false;
            }
            double headingError = normalizeAngle(targetHeading - currentHeading);
            turn = clamp(headingError * 0.5, -0.25, 0.25);
        }

        double speedScale = gamepad1.left_bumper ? 1.0 : 0.78;

        DrivePowers fieldCentricPowers = ManualDrive.fieldCentric(
                forward * speedScale,
                strafe * speedScale,
                turn * speedScale,
                currentHeading
        );

        follower.manual(fieldCentricPowers);
        follower.update();

        // Read nearest Hive AprilTag distance in the active consolidated pipeline
        LimelightClusterTracker.TagReading tagReading = clusterTracker.getNearestClusterTag();
        double effectiveDistInches = tagReading.valid
                ? tagReading.distanceInches
                : clusterTracker.getLastValidDistanceInches();

        // Edge-detected D-pad presses for clean +/- 50 TPS adjustments
        boolean dpadUp = gamepad1.dpad_up || gamepad2.dpad_up;
        boolean dpadDown = gamepad1.dpad_down || gamepad2.dpad_down;
        boolean dpadLeft = gamepad1.dpad_left || gamepad2.dpad_left;
        boolean dpadRight = gamepad1.dpad_right || gamepad2.dpad_right;

        boolean upEdge = dpadUp && !prevDpadUp;
        boolean downEdge = dpadDown && !prevDpadDown;
        boolean leftEdge = dpadLeft && !prevDpadLeft;
        boolean rightEdge = dpadRight && !prevDpadRight;

        prevDpadUp = dpadUp;
        prevDpadDown = dpadDown;
        prevDpadLeft = dpadLeft;
        prevDpadRight = dpadRight;

        // Record current (distance, pollTargetTps, nectTargetTps) point on gamepad2.left_stick_button
        boolean recordPointPressed = gamepad2.left_stick_button;
        if (recordPointPressed && !prevRecordPointPressed) {
            if (effectiveDistInches > 0.0) {
                double roundedDist = Math.round(effectiveDistInches * 2.0) / 2.0;
                distanceMap.recordCalibrationPoint(effectiveDistInches, pollTargetTps, nectTargetTps);
                lastRecordedCodeLine = "addPoint(" + roundedDist + ", "
                        + Math.round(pollTargetTps) + ".0, "
                        + Math.round(nectTargetTps) + ".0);";
            } else {
                lastRecordedCodeLine = "No valid tag distance to record";
            }
        }
        prevRecordPointPressed = recordPointPressed;

        // Plow servos
        if (gamepad2.left_bumper) {
            Plow.setPosition(0.0);
        } else if (gamepad2.right_bumper) {
            Plow.setPosition(0.95);
        }
        PlowL.setPosition(clamp(Plow.getPosition() + 0.05, 0.0, 1.0));

        // Intake
        double leftTrig = Math.max(gamepad1.left_trigger, gamepad2.left_trigger);
        double rightTrig = Math.max(gamepad1.right_trigger, gamepad2.right_trigger);
        if (gamepad1.left_trigger_pressed || gamepad2.left_trigger_pressed) {
            Intake.setPower(-leftTrig);
        } else {
            Intake.setPower(rightTrig);
        }

        // Fire kickers while holding cross
        if (gamepad1.cross || gamepad2.cross) {
            NectKick.setPower(-1.0);
            PllnKick2.setPower(-1.0);
            Intake.setPower(1.0);
        } else {
            NectKick.setPower(0.0);
            PllnKick2.setPower(0.0);
        }

        // Manual TPS tuning (+/- 50 TPS per tap)
        if (upEdge) nectTargetTps += TPS_STEP;
        if (leftEdge) nectTargetTps -= TPS_STEP;
        if (rightEdge) pollTargetTps += TPS_STEP;
        if (downEdge) pollTargetTps -= TPS_STEP;

        pollTargetTps = clamp(pollTargetTps, 0.0, MAX_SHOOTER_TPS);
        nectTargetTps = clamp(nectTargetTps, 0.0, MAX_SHOOTER_TPS);

        PllnKick.setPower(0.5 * PllnKick2.getPower());
        ShooterPIDF.updateTargets(pollTargetTps, nectTargetTps);

        // Stop shooters/kickers/intake on gamepad1.touchpad (keeps recorded map intact)
        if (gamepad1.touchpad) {
            NectKick.setPower(0.0);
            PllnKick.setPower(0.0);
            PllnKick2.setPower(0.0);
            Intake.setPower(0.0);
            pollTargetTps = 0.0;
            nectTargetTps = 0.0;
            ShooterPIDF.stopAll();
        }

        double currentPollVel = ShooterPIDF.getPollenVelocity();
        double currentNectVel = ShooterPIDF.getNectarVelocity();

        //telemetry.addLine("======= CALIBRATION MODE (MANUAL TPS) =======");
        //telemetry.addData("Loop ms", round1(lastLoopMs));

        telemetry.addLine("----------HIVE LIMELIGHT----------");
        telemetry.addData("Active Pipeline", getAllianceLabel(allianceIndex));
        telemetry.addData("Tag Visible", tagReading.valid);
        telemetry.addData("Nearest Tag ID", tagReading.valid ? tagReading.tagId : clusterTracker.getLastDetectedTagId());
        telemetry.addData("Live Dist (in)", tagReading.valid ? round1(tagReading.distanceInches) : -1.0);
        telemetry.addData("Last Valid Dist (in)", effectiveDistInches > 0.0 ? round1(effectiveDistInches) : -1.0);
        telemetry.addData("All Visible IDs", clusterTracker.getVisibleTagIdsString());

        telemetry.addLine("----------SHOOTERS (PIDF)--------");
        telemetry.addData("Pollen Target TPS", round1(pollTargetTps));
       // telemetry.addData("Pollen Actual TPS", round1(currentPollVel));
       // telemetry.addData("Pollen Current (A)", ShooterPIDF.getPollenCurrentAmps());
        telemetry.addData("Nectar Target TPS", round1(nectTargetTps));
       // telemetry.addData("Nectar Actual TPS", round1(currentNectVel));
       // telemetry.addData("Nectar Current (A)", ShooterPIDF.getNectarCurrentAmps());

        distanceMap.addTelemetry(telemetry, currentPollVel, currentNectVel);
        telemetry.addData("Last Saved Line", lastRecordedCodeLine);

        if (Intake instanceof DcMotorEx) {
            telemetry.addData("Intake Current (A)", ((DcMotorEx) Intake).getCurrent(CurrentUnit.AMPS));
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        ShooterPIDF.stopAll();
        clusterTracker.stop();
        if (PllnKick != null) PllnKick.setPower(0.0);
        if (PllnKick2 != null) PllnKick2.setPower(0.0);
        if (NectKick != null) NectKick.setPower(0.0);
        if (Intake != null) Intake.setPower(0.0);
        if (androidSoundPool != null) {
            androidSoundPool.close();
        }
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}