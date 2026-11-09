package org.firstinspires.ftc.teamcode.pedro.cb.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;

// PedroPath v3 Imports
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

@TeleOp(name = "CB111TWOCon_FieldCentric", group = "TeleOp")
@Disabled
public class TwoCon_java extends LinearOpMode {

    private Follower follower;
    private AndroidSoundPool androidSoundPool;
    private DcMotor NectShoot;
    private DcMotor PllnShoot;
    private CRServo NectKick;
    private Servo Gate;
    private DcMotor Intake;
    private VoltageSensor ControlHub_VoltageSensor;
    private DcMotor Separator;
    private CRServo PllnKick;
    private VoltageSensor ExpansionHub2_VoltageSensor;
    private final PoseFactory poseFactory  = PoseFactory.degrees();

    @Override
    public void runOpMode() {
        int GC_Shooter_Whl_Dia;
        int GC_encoder_count;
        int loop_count;
        boolean LeftstickStrafe;
        double PollShooterVel;
        int TPS_Poll_Corner;
        int TPS_Poll_Flower;
        int TPS_Poll_Wall;
        double NectShooterVel;
        int TPS_Nect_Corner;
        int TPS_Nect_Flower;
        int TPS_Next_Wall;
        ElapsedTime init_timer;
        ElapsedTime notification_timer;
        double Loop_time;
        double drive;
        double turn;
        double drive_speed;
        double strafe;

        // Initialize Follower using your tuned Constants class
        follower = Constants.create(hardwareMap);
        // --- SET STARTING POSE FROM AUTONOMOUS ---
        if (OpModeStorage.autonomousEndPose != null){
            follower.setPose(OpModeStorage.autonomousEndPose);
        } else {
            follower.setPose(new Pose(0, 0, 0));
        }

        androidSoundPool = new AndroidSoundPool();
        NectShoot = hardwareMap.get(DcMotor.class, "NectShoot");
        PllnShoot = hardwareMap.get(DcMotor.class, "PllnShoot");
        NectKick = hardwareMap.get(CRServo.class, "NectKick");
        Gate = hardwareMap.get(Servo.class, "Gate");
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        ControlHub_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        Separator = hardwareMap.get(DcMotor.class, "Separator");
        PllnKick = hardwareMap.get(CRServo.class, "PllnKick");
        ExpansionHub2_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Expansion Hub 2");

        androidSoundPool.initialize(SoundPlayer.getInstance());

        if (gamepad1.atRest() && gamepad2.atRest()) {
            androidSoundPool.play("RawRes:ss_wookie");
        } else {
            while (!(gamepad1.atRest() && gamepad2.atRest())) {
                androidSoundPool.play("RawRes:ss_siren");
                sleep(1000);
                telemetry.speak("Controller malfunction, unplug and reinsert", null, null);
                sleep(3500);
                telemetry.update();
            }
        }

        GC_Shooter_Whl_Dia = 75;
        GC_encoder_count = 28;
        loop_count = 0;
        LeftstickStrafe = false;

        NectShoot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PllnShoot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PollShooterVel = 0;
        TPS_Poll_Corner = 2800;
        TPS_Poll_Flower = 2400;
        TPS_Poll_Wall = 1860;
        PllnShoot.setDirection(DcMotor.Direction.REVERSE);

        NectShooterVel = 0;
        TPS_Nect_Corner = 2800;
        TPS_Nect_Flower = 2300;
        TPS_Next_Wall = 1950;
        NectKick.setDirection(CRServo.Direction.REVERSE);

        init_timer = new ElapsedTime();
        notification_timer = new ElapsedTime();
        Gate.setPosition(1);
        Intake.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        if (opModeIsActive()) {
            telemetry.speak("Let's do this", null, null);

            while (opModeIsActive()) {
                loop_count += 1;
                Loop_time = (new ElapsedTime()).milliseconds() / loop_count;

                // Required PedroPath loop update
                follower.update();

                if (ControlHub_VoltageSensor.getVoltage() < 9) {
                    androidSoundPool.play("RawRes:ss_alarm");
                    telemetry.speak("Battery Voltage Low", null, null);
                }

                // Toggle stick layout button check
                if (gamepad1.left_stick_button && gamepad1.right_stick_button) {
                    LeftstickStrafe = !LeftstickStrafe;
                }

                drive = -gamepad1.left_stick_y;

                if (LeftstickStrafe == false) {
                    turn = gamepad1.right_stick_x * (1 - Math.abs(gamepad1.left_stick_y) / 2);
                    strafe = gamepad1.left_stick_x;
                } else {
                    turn = gamepad1.left_stick_x * (1 - Math.abs(gamepad1.left_stick_y) / 2);
                    strafe = gamepad1.right_stick_x;
                }

                if (gamepad1.left_bumper) {
                    drive_speed = 1.3;
                } else {
                    drive_speed = 1;
                }

                // --- FIELD-CENTRIC DRIVING (PedroPath Documentation Standard) ---
                DrivePowers fieldCentricPowers = ManualDrive.fieldCentric(
                        -gamepad1.left_stick_y * drive_speed,
                        gamepad1.left_stick_x * drive_speed,
                        gamepad1.right_stick_x * drive_speed,
                        follower.pose().heading()
                );

                // Pass the calculated field-centric powers to the follower
                follower.manual(fieldCentricPowers);


                // Intake / separate logic
                if (gamepad1.right_bumper || gamepad2.right_bumper) {
                    Separator.setPower(1);
                } else {
                    Separator.setPower(0);
                }

                if (gamepad1.left_trigger_pressed || gamepad2.left_trigger_pressed) {
                    Intake.setPower(-gamepad1.left_trigger);
                    Intake.setPower(-gamepad2.left_trigger);
                    Separator.setPower(-gamepad1.left_trigger);
                    Separator.setPower(-gamepad2.left_trigger);
                    Gate.setPosition(1);
                } else if (Gate.getPosition() == 0) {
                    if (gamepad1.right_trigger >= gamepad2.right_trigger) {
                        Intake.setPower(gamepad1.right_trigger + 0.4);
                    } else {
                        Intake.setPower(gamepad2.right_trigger + 0.4);
                    }
                } else {
                    if (gamepad1.right_trigger >= gamepad2.right_trigger) {
                        Intake.setPower(gamepad1.right_trigger);
                    } else {
                        Intake.setPower(gamepad2.right_trigger);
                    }
                }

                if (gamepad1.rightTriggerWasReleased() || gamepad2.rightTriggerWasReleased()) {
                    PllnKick.setPower(1);
                }

                if (gamepad1.ps) {
                    Gate.setPosition(0);
                } else if (gamepad1.cross || gamepad2.cross) {
                    Gate.setPosition(1);
                    Intake.setPower(0.75);
                    Separator.setPower(1);
                }

                // Shooter velocity control
                ((DcMotorEx) NectShoot).setVelocity(NectShooterVel);
                ((DcMotorEx) PllnShoot).setVelocity(PollShooterVel);
                NectShooterVel = Math.min(Math.max(NectShooterVel, 0), 2800);
                PollShooterVel = Math.min(Math.max(PollShooterVel, 0), 2800);

                if (gamepad1.square || gamepad2.square) {
                    PollShooterVel = TPS_Poll_Corner;
                    NectShooterVel = TPS_Nect_Corner;
                    NectKick.setPower(-1);
                    PllnKick.setPower(-1);
                    Separator.setPower(1);
                } else if (gamepad1.triangle || gamepad2.triangle) {
                    PollShooterVel = TPS_Poll_Flower;
                    NectShooterVel = TPS_Nect_Flower;
                    NectKick.setPower(-1);
                    PllnKick.setPower(-1);
                    Separator.setPower(1);
                } else if (gamepad1.circle || gamepad2.circle) {
                    PollShooterVel = TPS_Poll_Wall;
                    NectShooterVel = TPS_Next_Wall;
                    NectKick.setPower(-0.7);
                    PllnKick.setPower(-0.7);
                    Separator.setPower(1);
                } else if (gamepad1.cross || gamepad2.cross) {
                    NectKick.setPower(-0.7);
                    PllnKick.setPower(-0.7);
                    Separator.setPower(1);
                } else {
                    if (gamepad1.dpad_up || gamepad2.dpad_up) {
                        NectShooterVel = NectShooterVel + 50;
                    } else if (gamepad1.dpad_right || gamepad2.dpad_right) {
                        PollShooterVel = PollShooterVel + 50;
                    } else if (gamepad1.dpad_down || gamepad2.dpad_down) {
                        PollShooterVel = PollShooterVel - 50;
                    } else if (gamepad1.dpad_left || gamepad2.dpad_left) {
                        NectShooterVel = NectShooterVel - 50;
                    }
                    NectKick.setPower(0);
                    PllnKick.setPower(0);
                }

                // All Stop
                if (gamepad1.touchpad) {
                    NectKick.setPower(0);
                    PllnKick.setPower(0);
                    Separator.setPower(0);
                    Intake.setPower(0);
                    NectShoot.setPower(0);
                    PllnShoot.setPower(0);
                }

                if (gamepad1.share || gamepad2.share  ) {

                    if(OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.RED_UP) {
                        Pose cornerPose = new Pose(9, 8.385, -90);
                        follower.setPose(cornerPose);
                    } else if(OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.RED_DOWN){
                        Pose cornerPose = new Pose(9, 135, 90);
                        follower.setPose(cornerPose);
                    } else if (OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.BLUE_UP){
                        Pose cornerPose = new Pose(135, 135.615, 90);
                        follower.setPose(cornerPose);
                    }else if (OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.BLUE_DOWN){
                        Pose cornerPose = new Pose(135, 8.385, -90);
                        follower.setPose(cornerPose);
                    }


                }

                // Telemetry
                telemetry.addData("Loop Time (ms)", Math.round(Loop_time * 1000) / 1000);
                telemetry.addData("Left stick strafe", LeftstickStrafe);
                telemetry.addData("Field X (Inches)", follower.pose().x());
                telemetry.addData("Field Y (Inches)", follower.pose().y());
                telemetry.addData("Heading (Deg)", Math.toDegrees(follower.pose().heading()));
                telemetry.update();
            }
        }

        androidSoundPool.close();
    }
}