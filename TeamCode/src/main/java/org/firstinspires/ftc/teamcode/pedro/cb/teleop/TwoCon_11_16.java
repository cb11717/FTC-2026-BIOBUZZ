package org.firstinspires.ftc.teamcode.pedro.cb.teleop;

import com.pedropathing.math.Pose;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.limelightvision.Limelight3A;
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

// Pedro Pathing Imports for Field-Centric Manual Drive
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.drivetrain.DrivePowers;

@TeleOp(name = "TwoCon_11_16")
@Disabled
public class TwoCon_11_16 extends LinearOpMode {

    private AndroidSoundPool androidSoundPool;
    private DcMotor NectShoot;
    private DcMotor PllnShoot;
    private Servo Plow;
    private Servo PlowL;
    private CRServo PllnKick;
    private CRServo PllnKick2;
    private Limelight3A limelight;
    private VoltageSensor ControlHub_VoltageSensor;
    private DcMotor Intake;
    private CRServo NectKick;
    private VoltageSensor ExpansionHub2_VoltageSensor;

    // Pedro Pathing Follower
    private Follower follower;

    @Override
    public void runOpMode() {
        int loop_count;
        boolean LeftstickStrafe;
        double PollShooterVel;
        int TPS_Poll_Corner;
        int TPS_Poll_Flower;
        int TPS_Poll_Wall;
        double NectShooterVel;
        int TPS_Nect_Corner;
        int TPS_Nect_Flower;
        int TPS_Nect_Wall;
        double Loop_time;
        double drive_speed;
        float strafe;
        float forward;
        float turn;

        androidSoundPool = new AndroidSoundPool();
        NectShoot = hardwareMap.get(DcMotor.class, "NectShoot");
        PllnShoot = hardwareMap.get(DcMotor.class, "PllnShoot");
        Plow = hardwareMap.get(Servo.class, "Plow");
        PlowL = hardwareMap.get(Servo.class, "PlowL");
        PllnKick = hardwareMap.get(CRServo.class, "PllnKick");
        PllnKick2 = hardwareMap.get(CRServo.class, "PllnKick2");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        ControlHub_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        NectKick = hardwareMap.get(CRServo.class, "NectKick");
        ExpansionHub2_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Expansion Hub 2");

        // Initialize Pedro Pathing Follower using Constants
        follower = Constants.create(hardwareMap);

        if (OpModeStorage.autonomousEndPose != null){
            follower.setPose(OpModeStorage.autonomousEndPose);
        } else {
            follower.setPose(new Pose(0, 0, 0));
        }

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

        loop_count = 0;
        LeftstickStrafe = false;

        NectShoot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PllnShoot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Plow.setPosition(0);
        PlowL.setPosition(0);
        PollShooterVel = 0;
        TPS_Poll_Corner = 2300;
        TPS_Poll_Flower = 1800;
        TPS_Poll_Wall = 1490;
        PllnShoot.setDirection(DcMotor.Direction.REVERSE);
        NectShooterVel = 0;
        TPS_Nect_Corner = 2300;
        TPS_Nect_Flower = 2200;
        TPS_Nect_Wall = 2060;
        PllnKick.setDirection(CRServo.Direction.REVERSE);
        PllnKick2.setDirection(CRServo.Direction.REVERSE);

        waitForStart();
        if (opModeIsActive()) {
            telemetry.speak("Let's do this", null, null);
            limelight.stop();

            while (opModeIsActive()) {
                loop_count += 1;
                Loop_time = (new ElapsedTime()).milliseconds() / loop_count;

                // CRITICAL: Update follower position/heading every loop for field-centric tracking
                follower.update();

                if (ControlHub_VoltageSensor.getVoltage() < 9) {
                    androidSoundPool.play("RawRes:ss_alarm");
                    telemetry.speak("Battery Voltage Low", null, null);
                }

                // Drive Logic: Toggle stick mapping
                if (gamepad1.left_stick_button && gamepad1.right_stick_button) {
                    LeftstickStrafe = !LeftstickStrafe;
                }

                float rawForward = -gamepad1.left_stick_y;
                float rawStrafe;

                if (!LeftstickStrafe) {
                    turn = gamepad1.right_stick_x * (1.0f - Math.abs(gamepad1.left_stick_y) / 2.0f);
                    rawStrafe = gamepad1.left_stick_x;
                } else {
                    turn = gamepad1.left_stick_x * (1.0f - Math.abs(gamepad1.left_stick_y) / 2.0f);
                    rawStrafe = gamepad1.right_stick_x;
                }

                if (gamepad1.left_bumper) {
                    drive_speed = 1.3;
                } else {
                    drive_speed = 1.0;
                }

                // Apply speed scaling to inputs
                forward = (float) (rawForward * drive_speed);
                strafe = (float) (rawStrafe * drive_speed);
                float scaledTurn = (float) (turn * drive_speed);

                // =========================================================================
                // FIELD-CENTRIC DRIVING (PedroPath Documentation Standard)
                // =========================================================================
                DrivePowers fieldCentricPowers = ManualDrive.fieldCentric(
                        forward,
                        strafe,
                        scaledTurn,
                        follower.pose().heading()
                );

                // Pass calculated field-centric powers to the Pedro follower
                follower.manual(fieldCentricPowers);

                // Plow
                PlowL.setPosition(Plow.getPosition() + 0);
                if (gamepad2.left_bumper) {
                    Plow.setPosition(0);
                } else if (gamepad2.right_bumper) {
                    Plow.setPosition(0.93);
                }

                // Intake
                if (gamepad1.left_trigger_pressed || gamepad2.left_trigger_pressed) {
                    if (gamepad1.left_trigger >= gamepad2.left_trigger) {
                        Intake.setPower(-gamepad1.left_trigger);
                    } else {
                        Intake.setPower(-gamepad2.left_trigger);
                    }
                } else {
                    if (gamepad1.right_trigger >= gamepad2.right_trigger) {
                        Intake.setPower(gamepad1.right_trigger);
                    } else {
                        Intake.setPower(gamepad2.right_trigger);
                    }
                }
                if (gamepad1.rightTriggerWasReleased() || gamepad2.rightTriggerWasReleased()) {
                    PllnKick2.setPower(1);
                }

                // Shooter - velocity control
                ((DcMotorEx) NectShoot).setVelocity(NectShooterVel);
                ((DcMotorEx) PllnShoot).setVelocity(PollShooterVel);
                NectShooterVel = Math.min(Math.max(NectShooterVel, 0), 2800);
                PollShooterVel = Math.min(Math.max(PollShooterVel, 0), 2800);
                PllnKick.setPower(0.5 * PllnKick2.getPower());

                if (gamepad1.square || gamepad2.square) {
                    PollShooterVel = TPS_Poll_Corner;
                    NectShooterVel = TPS_Nect_Corner;
                    NectKick.setPower(-1);
                    PllnKick2.setPower(-1);
                    Intake.setPower(1);
                } else if (gamepad1.triangle || gamepad2.triangle) {
                    PollShooterVel = TPS_Poll_Flower;
                    NectShooterVel = TPS_Nect_Flower;
                    NectKick.setPower(-1);
                    PllnKick2.setPower(-1);
                    Intake.setPower(1);
                } else if (gamepad1.circle || gamepad2.circle) {
                    PollShooterVel = TPS_Poll_Wall;
                    NectShooterVel = TPS_Nect_Wall;
                    if (((DcMotorEx) PllnShoot).getVelocity() < TPS_Poll_Wall + 15 && ((DcMotorEx) PllnShoot).getVelocity() > TPS_Poll_Wall - 15) {
                        PllnKick2.setPower(-1);
                    }
                    if (((DcMotorEx) NectShoot).getVelocity() < TPS_Nect_Wall + 15 && ((DcMotorEx) NectShoot).getVelocity() > TPS_Nect_Wall - 15) {
                        NectKick.setPower(-1);
                    }
                    Plow.setPosition(0.1);
                } else if (gamepad1.cross || gamepad2.cross) {
                    NectKick.setPower(-1);
                    PllnKick2.setPower(-1);
                    Intake.setPower(1);
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
                    PllnKick2.setPower(0);
                }

                // All Stop
                if (gamepad1.touchpad) {
                    NectKick.setPower(0);
                    PllnKick.setPower(0);
                    Intake.setPower(0);
                    NectShooterVel = 0;
                    PollShooterVel = 0;
                }

                if (gamepad1.share || gamepad2.share  ) {

                    if(OpModeStorage.selectedPosition == OpModeStorage.StartingPosition.UNDEFINED){
                        Pose cornerPose = new Pose(9, 8.385, -90);
                        follower.setPose(cornerPose);
                    }
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
                telemetry.addData("Loop Time - ms per loop", Math.round(Loop_time * 1000) / 1000);
                telemetry.addData("Heading (Deg)", Math.toDegrees(follower.pose().heading()));
                telemetry.addData("Left stick strafe", LeftstickStrafe);
                telemetry.addData("Exp Hub Voltage Drop", Math.round((ControlHub_VoltageSensor.getVoltage() - ExpansionHub2_VoltageSensor.getVoltage()) * 1000) / 1000);
                telemetry.addLine("----------SHOOTERS--------");
                telemetry.addData("Nectar TPS", ((DcMotorEx) NectShoot).getVelocity());
                telemetry.addData("Pollen TPS", ((DcMotorEx) PllnShoot).getVelocity());
                telemetry.update();
            }
        }

        androidSoundPool.close();
    }
}