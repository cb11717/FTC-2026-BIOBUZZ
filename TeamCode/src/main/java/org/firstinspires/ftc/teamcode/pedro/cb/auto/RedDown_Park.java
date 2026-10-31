package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name="RedDown_Park", group="Auto", preselectTeleOp="TeleOpJava")
public class RedDown_Park extends OpMode {

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    private final Pose start = p.of(59, 132, 90);
    private final Pose straightPark = p.of(9, 106, 90);
    private final Pose straightParkControl1 = p.of(60, 109, 0);

    public Path straightPark() {
        return Paths.curve(start, straightParkControl1, straightPark).linear(start, straightPark);
    }

    private Command autoRoutine() {
        return sequential(
                // Add mechanism commands here.
                follow(follower, straightPark())
        );
    }

    @Override
    public void loop(){

        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }

    @Override
    public void init() {

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        schedule(autoRoutine());
        // using the same park path from the last page
    }

    @Override
    public void stop() {

    }
}