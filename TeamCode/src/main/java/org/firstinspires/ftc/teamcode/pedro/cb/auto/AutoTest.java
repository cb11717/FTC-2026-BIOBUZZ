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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.race;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Gate;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Intake;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Plow;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Separator;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.TelemetryUtil;

import static com.pedropathing.api.Paths.*;

@Autonomous(name="AutoTest", group="Auto", preselectTeleOp="TeleOpJava")

public class AutoTest extends OpMode {

    int TPS_Poll_Wall = 2000;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    private final Pose start = p.of(24, 0, 90); //This is our starting point
    private final Pose end24Y = p.of(24, 24, 90);

    public Path straightLineY24() {
        return line(start, end24Y).linear(start, end24Y);
    }


    private Command autoRoutine() {
        return sequential(

                race(

                        Plow.run(0), //0 means the plow is up and 1 is Plow mode
                        waitMs(15000)
                )
/*
                race(
                        waitUntil(() -> {
                            if (vision.getVisibleClusterTagCount() > 0) {
                                this.tagFound = true;
                                return true; // Ends the race early if tag is seen!
                            }
                            return false;
                        }),
                        waitMs(15000) // 15-second timeout fallback
                ),

                // 2. Conditional: If tag was found within 15s, run program. Else, park.
                conditional(

                        () -> this.tagFound,
                        // TRUE: Full scoring routine
                        sequential(
                                TelemetryUtil.log("AprilTag" ,"***FOUND APRIL TAG***"),
                                waitMs(1000)
                        ),
                        // FALSE
                        sequential (
                                TelemetryUtil.log("AprilTag" ,"NO"),
                                waitMs(1000)
                        )
                )
*/
                //Kicker.run(-1.0)
                /*
                race(

                        Kicker.run(0.5),
                        waitMs(15000)
                )

                */
                /*
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
                        Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                        Kicker.run(-1),
                        Separator.run(1),
                        Gate.run(0)
                    ),
                    waitMs(3000)
                )
                */
        );
    }

    @Override
    public void loop(){

        follower.update();
        //Kicker.forcePower(-0.6);
        Scheduler.execute();
        // add your other methods needed in the loop here



        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.addData("Kicker Loop Count", Kicker.loopCounter);
        telemetry.addData("Kicker Interrupted", Kicker.KickerInterrupted);

        telemetry.update();
    }

    @Override
    public void init() {

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
        //Gate.init(hardwareMap);
        //Separator.init(hardwareMap);
        Intake.init(hardwareMap);
        Kicker.init(hardwareMap);
        Shooter.init(hardwareMap);
        Plow.init(hardwareMap);

        //Blue Auto Down
        vision.init(hardwareMap, 1, LimelightClusterTracker.BLUE_AUDIENCE_IDS);
        // --- INITIALIZE TELEMETRY UTILITY HERE ---
        TelemetryUtil.init(telemetry);


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