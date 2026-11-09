package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.race;
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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;

@Autonomous(name="RedUp_Park", group="Auto", preselectTeleOp="CB111TWOCon")

public class RedUp_Park extends OpMode {

    int TPS_Poll_Wall = 1500;
    int TPS_Nect_Wall = 1900;

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    private final Pose start = p.of(56, 9, -90);
    private final Pose straightPark = p.of(12, 92, -90);
    private final Pose straightParkControl1 = p.of(18, 8, 0);

    public Path straightPark() {
        return Paths.curve(start, straightParkControl1, straightPark).linear(start, straightPark);
    }

    private Command autoRoutine() {
        return parallel(
                // 1. MECHANISMS: Run continuously in the background
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                sequential(
                        waitMs(4000),           // Spin up shooter for 0.5 seconds
                        Kicker.run(-1.0)       // Then start the kicker and keep it running
                ),

                // 2. MOVEMENT: Actions happen while the mechanisms stay on
                sequential(
                        waitMs(8500),          // Stay in place for 3 seconds shooting/scoring
                        follow(follower, straightPark()) // Drive to park while mechanisms are still running
                )
        );
    }
    /*
    private Command autoRoutine() {
        return sequential(
                race(

                        Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                        // Kicker waits 0.5 seconds, then starts
                        sequential(
                                waitMs(1000),
                                Kicker.run(-1.0)
                        ),
                        waitMs(3000)
                ),
                // Add mechanism commands here.
                follow(follower, straightPark())
        );
    }
    */


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
        Shooter.init(hardwareMap);
        Kicker.init(hardwareMap);
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