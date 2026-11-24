package org.firstinspires.ftc.teamcode.pedro.cb.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.ShooterPIDF;

@TeleOp(name = "CB112TWOCon10nov261748 (Blocks to Java)")
public class CB112TWOCon10nov261748 extends OpMode {

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private Follower follower;

    private AndroidSoundPool androidSoundPool;
    private Servo Plow;
    private Servo PlowL;
    private CRServo PllnKick;
    private CRServo PllnKick2;
    private CRServo NectKick;
    private DcMotor Intake;
    private Limelight3A limelight;
    private VoltageSensor ControlHub_VoltageSensor;
    private VoltageSensor ExpansionHub2_VoltageSensor;

    private boolean leftstickStrafe = false;
    private boolean prevStickTogglePressed = false;
    private boolean controllerCheckDone = false;

    private double pollTargetTps = 0.0;
    private double nectTargetTps = 0.0;

    private final int TPS_Poll_Corner = 2300;
    private final int TPS_Poll_Flower = 1800;
    private final int TPS_Poll_Wall = 1490;

    private final int TPS_Nect_Corner = 2300;
    private final int TPS_Nect_Flower = 2200;
    private final int TPS_Nect_Wall = 2075;

    private final ElapsedTime loopTimer = new ElapsedTime();
    private double lastLoopMs = 0.0;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
/*
        Pose initialPose = (OpModeStorage.selectedPosition != OpModeStorage.StartingPosition.UNDEFINED
                && OpModeStorage.autonomousEndPose != null)
                ? OpModeStorage.autonomousEndPose
                : poseFactory.of(0, 0, 0);

 */
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

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        ControlHub_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        ExpansionHub2_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Expansion Hub 2");

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

    @Override
    public void init_loop() {
        follower.update();
        if (!controllerCheckDone) {
            if (gamepad1.atRest() && gamepad2.atRest()) {
                androidSoundPool.play("RawRes:ss_wookie");
            } else {
                telemetry.addLine("Controller malfunction: check gamepads are at rest.");
            }
            controllerCheckDone = true;
        }
        telemetry.update();
    }

    @Override
    public void start() {
        if (limelight != null) {
            limelight.stop();
        }
        loopTimer.reset();
        telemetry.speak("Let's do this", null, null);
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

        // Relocalize pose using degree headings via PoseFactory
        if (gamepad1.share || gamepad2.share) {
            Pose cornerPose;
            if (OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.RED_DOWN) {
                cornerPose = poseFactory.of(8.5, 135, 90);
            } else if (OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.BLUE_UP) {
                cornerPose = poseFactory.of(135, 135.615, 90);
            } else if (OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.BLUE_DOWN) {
                cornerPose = poseFactory.of(135, 8.5, -90);
            } else {
                // RED_UP or UNDEFINED default
                cornerPose = poseFactory.of(8.5, 8.5, -90);
            }
            follower.setPose(cornerPose);
        }

        // Toggle stick strafe mapping on rising edge of pressing both sticks
        boolean stickTogglePressed = gamepad1.left_stick_button && gamepad1.right_stick_button;
        if (stickTogglePressed && !prevStickTogglePressed) {
            leftstickStrafe = !leftstickStrafe;
        }
        prevStickTogglePressed = stickTogglePressed;

        double forward = -gamepad1.left_stick_y;
        double strafe;
        double turn;
        double turnScale = 1.0 - (Math.abs(gamepad1.left_stick_y) / 2.0);

        if (!leftstickStrafe) {
            strafe = gamepad1.left_stick_x;
            turn = gamepad1.right_stick_x * turnScale;
        } else {
            strafe = gamepad1.right_stick_x;
            turn = gamepad1.left_stick_x * turnScale;
        }

        double speedScale = gamepad1.left_bumper ? 1.0 : 0.78;

        // PedroPathing 3.0.1 field-centric drive (currentHeading in radians)
        DrivePowers fieldCentricPowers = ManualDrive.fieldCentric(
                forward * speedScale,
                strafe * speedScale,
                turn * speedScale,
                follower.pose().heading()
        );

        follower.manual(fieldCentricPowers);
        follower.update();

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

        // Shooter speed presets & kickers
        if (gamepad1.square || gamepad2.square) {
            pollTargetTps = TPS_Poll_Corner;
            nectTargetTps = TPS_Nect_Corner;
            NectKick.setPower(-1.0);
            PllnKick2.setPower(-1.0);
            Intake.setPower(1.0);
        } else if (gamepad1.triangle || gamepad2.triangle) {
            pollTargetTps = TPS_Poll_Flower;
            nectTargetTps = TPS_Nect_Flower;
            NectKick.setPower(-1.0);
            PllnKick2.setPower(-1.0);
            Intake.setPower(1.0);
        } else if (gamepad1.circle || gamepad2.circle) {
            pollTargetTps = TPS_Poll_Wall;
            nectTargetTps = TPS_Nect_Wall;

            double currentPoll = ShooterPIDF.getPollenVelocity();
            double currentNect = ShooterPIDF.getNectarVelocity();

            if (Math.abs(currentPoll - pollTargetTps) <= 50.0) {
                PllnKick2.setPower(-1.0);
            } else {
                PllnKick2.setPower(0.0);
            }

            if (Math.abs(currentNect - nectTargetTps) <= 50.0) {
                NectKick.setPower(-0.7);
            } else {
                NectKick.setPower(0.0);
            }
            Plow.setPosition(0.1);
        } else if (gamepad1.cross || gamepad2.cross) {
            NectKick.setPower(-1.0);
            PllnKick2.setPower(-1.0);
            Intake.setPower(1.0);
        } else {
            if (gamepad1.dpad_up || gamepad2.dpad_up) {
                nectTargetTps += 50.0;
            } else if (gamepad1.dpad_right || gamepad2.dpad_right) {
                pollTargetTps += 50.0;
            } else if (gamepad1.dpad_down || gamepad2.dpad_down) {
                pollTargetTps -= 50.0;
            } else if (gamepad1.dpad_left || gamepad2.dpad_left) {
                nectTargetTps -= 50.0;
            }
            NectKick.setPower(0.0);
            PllnKick2.setPower(0.0);
        }

        pollTargetTps = clamp(pollTargetTps, 0.0, 2800.0);
        nectTargetTps = clamp(nectTargetTps, 0.0, 2800.0);

        PllnKick.setPower(0.5 * PllnKick2.getPower());

        // Apply PIDF velocity control for both Pollen and Nectar
        ShooterPIDF.updateTargets(pollTargetTps, nectTargetTps);

        // All Stop
        if (gamepad1.touchpad) {
            NectKick.setPower(0.0);
            PllnKick.setPower(0.0);
            PllnKick2.setPower(0.0);
            Intake.setPower(0.0);
            pollTargetTps = 0.0;
            nectTargetTps = 0.0;
            ShooterPIDF.stopAll();
        }

        // Telemetry placed after follower and shooter updates
        telemetry.addData("Loop ms", Math.round(lastLoopMs * 10) / 10.0);
        telemetry.addData("Selected Pos", OpModeStorage.selectedPosition);
        telemetry.addData("Field X (in)", follower.pose().x());
        telemetry.addData("Field Y (in)", follower.pose().y());
        telemetry.addData("Heading (deg)", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Left Stick Strafe", leftstickStrafe);

        if (ControlHub_VoltageSensor != null && ExpansionHub2_VoltageSensor != null) {
            double vDrop = ControlHub_VoltageSensor.getVoltage() - ExpansionHub2_VoltageSensor.getVoltage();
            telemetry.addData("Exp Hub Voltage Drop", Math.round(vDrop * 1000.0) / 1000.0);
        }

        telemetry.addLine("----------SHOOTERS (PIDF)--------");
        telemetry.addData("Pollen Target TPS", pollTargetTps);
        telemetry.addData("Pollen Actual TPS", ShooterPIDF.getPollenVelocity());
        telemetry.addData("Pollen Current (A)", ShooterPIDF.getPollenCurrentAmps());
        telemetry.addData("Nectar Target TPS", nectTargetTps);
        telemetry.addData("Nectar Actual TPS", ShooterPIDF.getNectarVelocity());
        telemetry.addData("Nectar Current (A)", ShooterPIDF.getNectarCurrentAmps());

        if (Intake instanceof DcMotorEx) {
            telemetry.addData("Intake Current (A)", ((DcMotorEx) Intake).getCurrent(CurrentUnit.AMPS));
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        ShooterPIDF.stopAll();
        if (PllnKick != null) PllnKick.setPower(0.0);
        if (PllnKick2 != null) PllnKick2.setPower(0.0);
        if (NectKick != null) NectKick.setPower(0.0);
        if (Intake != null) Intake.setPower(0.0);
        if (androidSoundPool != null) {
            androidSoundPool.close();
        }
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}