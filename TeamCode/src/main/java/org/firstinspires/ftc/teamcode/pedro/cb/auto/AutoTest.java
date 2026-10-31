package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.race;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Gate;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Intake;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Separator;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;

@Autonomous(name="AutoTest", group="Auto", preselectTeleOp="TeleOpJava")
public class AutoTest extends OpMode {

    int TPS_Poll_Wall = 1900;
    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    private final Pose start = p.of(24, 0, 90); //This is our starting point
    private final Pose end24Y = p.of(24, 24, 90);

    public Path straightLineY24() {
        return line(start, end24Y).linear(start, end24Y);
    }

    private Command autoRoutine() {
        return sequential(
                // Add mechanism commands here.
                follow(follower, straightLineY24()),
                // 2. Run Intake, Gate, and Separator for 1 second, then stop them automatically
                race(
                        parallel(
                                Intake.run(0.75),
                                Gate.run(1),
                                Separator.run(1)
                        ),
                        waitMs(3000)
                ),
                race(
                    parallel(
                        Shooter.run(TPS_Poll_Wall),
                        Kicker.run(-1),
                        Separator.run(1),
                        Gate.run(0)
                    ),
                    waitMs(3000)
                )
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
        Gate.init(hardwareMap);
        Separator.init(hardwareMap);
        Intake.init(hardwareMap);
        Kicker.init(hardwareMap);
        Shooter.init(hardwareMap);


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