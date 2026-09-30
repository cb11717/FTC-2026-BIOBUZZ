package org.firstinspires.ftc.teamcode.pedro.cb.teleop;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;

@TeleOp(name = "TeleOpJava", group = "TeleOp")
public class TeleopPedroExample extends OpMode {
    private Follower follower;
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
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);

        // relocalise button
        if (gamepad1.left_trigger_pressed) {
            //TODO: revisit the coordinates after the robot is built so as to set accurate dimension
            Pose cornerPose = new Pose(10.5, 10.5, Math.toRadians(90));
            // On the fly Pose creation, we dont recommend this for Autonomous. Only accepts radians for heading
            follower.setPose(cornerPose); // overrides our pose
        }

        follower.update();

        Pose robotPose = follower.pose(); // returns a Pose object
        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());
        telemetry.addData("Robot Heading", Math.toDegrees(robotPose.heading()));
    }

}


