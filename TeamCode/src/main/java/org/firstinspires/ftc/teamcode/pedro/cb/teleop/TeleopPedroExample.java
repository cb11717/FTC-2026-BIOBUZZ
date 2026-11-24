package org.firstinspires.ftc.teamcode.pedro.cb.teleop;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;

@TeleOp(name = "TeleOpJava", group = "TeleOp")
public class TeleopPedroExample extends OpMode {
    private Follower follower;

    // Add these fields to your TeleOp class:
    private double targetHeading = 0.0;
    private boolean wasTurning = false;

    private static double deadzone(double value, double dz) {
        return Math.abs(value) < dz ? 0.0 : value;
    }

    private static double normalizeAngle(double angleRad) {
        return Math.atan2(Math.sin(angleRad), Math.cos(angleRad));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start(){

        // Set starting pose: use autoEndPose if available, otherwise fallback to (0, 0, 0)
        if (OpModeStorage.autonomousEndPose != null){
            follower.setPose(OpModeStorage.autonomousEndPose);
        } else {
            follower.setPose(new Pose(0, 0, 0));
        }

        follower.update();

    }

    @Override
    public void loop() {

        double forward = deadzone(-gamepad1.left_stick_y, 0.05);
        double strafe  = deadzone(gamepad1.left_stick_x, 0.05);
        double turn    = deadzone(gamepad1.right_stick_x, 0.05);

        double currentHeading = follower.pose().heading();

        if (turn != 0.0) {
            wasTurning = true;
        } else {
            if (wasTurning) {
                targetHeading = currentHeading;
                wasTurning = false;
            }
            double headingError = normalizeAngle(targetHeading - currentHeading);
            // Start small (e.g., 0.8 to 1.5) and increase gradually only if needed;
            // too high a gain will cause the robot to wiggle/oscillate left and right.
            turn = clamp(headingError * 0.5, -0.25, 0.25);
        }
        DrivePowers powers = ManualDrive.fieldCentric(
                forward,
                strafe,
                turn,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);



        // relocalise button
        if (gamepad1.left_trigger_pressed) {
            //TODO: revisit the coordinates after the robot is built so as to set accurate dimension
            Pose cornerPose = new Pose(10.5, 10.5, Math.toRadians(90));
            // On the fly Pose creation, we dont recommend this for Autonomous. Only accepts radians for heading
            follower.setPose(cornerPose); // overrides our pose
            targetHeading = cornerPose.heading();
            wasTurning = false;
        }

        follower.update();

        Pose robotPose = follower.pose(); // returns a Pose object
        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());
        telemetry.addData("Robot Heading", Math.toDegrees(robotPose.heading()));

        telemetry.addData("RX", gamepad1.right_stick_x);
        telemetry.addData("LX", gamepad1.left_stick_x);
        telemetry.addData("LY", gamepad1.left_stick_y);
        telemetry.update();
    }

}


