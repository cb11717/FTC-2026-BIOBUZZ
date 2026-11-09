package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import static com.pedropathing.api.Paths.*;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.race;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.pedropathing.ivy.Command;
import static com.pedropathing.ivy.commands.Commands.*;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Gate;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Intake;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Plow;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Separator;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;

@Autonomous(name="RedAutoDown", group="Auto", preselectTeleOp="TeleOpJava")
@Disabled
public class RedAutoDown extends OpMode {

    int TPS_Poll_Wall = 1500;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    //our poses
    private final Pose start = p.of(59, 132, 90);
    private final Pose flower = p.of(48, 130, 90);
    private final Pose flowerControlPoint = p.of(58, 109, 0);
    private final Pose shootPollen = p.of(60, 132, 90);
    private final Pose shootPollenControlPoint = p.of(58, 109, 0);
    private final Pose park = p.of(10, 117, 90);
    private final Pose parkControlPoint = p.of(60, 109, 0);

    public Path flower() {
        return curve(start, flowerControlPoint, flower).linear(start, flower);
    }

    public Path shootPollen() {
        return curve(flower, shootPollenControlPoint, shootPollen).linear(flower, shootPollen);
    }

    public Path park() {
        return curve(shootPollen, parkControlPoint, park).constant(park);
    }

    private Command autoRoutine() {
       // int clusterCount = vision.getVisibleClusterTagCount();

        return sequential(
            // 1. Race: Wait up to 15 seconds for the AprilTag to appear
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
                    race(
                            parallel(
                                 Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                                 Kicker.run(-1.0)
                            ),
                            waitMs(3000)
                    ),
                    parallel(
                         Plow.run(1), //Plow mode
                        follow(follower, flower())
                    ),
                    race(
                            parallel(
                                    Intake.run(0.75)
                                    //Gate.run(1),
                                    //Separator.run(1)
                            ),
                            waitMs(3000)
                    ),
                    parallel(
                            Plow.run(0),
                            follow(follower, shootPollen()),
                            Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                            Intake.run(0)
                           // Gate.run(0),
                            //Separator.run(0)
                    ),
                    race(
                            parallel(
                                    Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                                    Kicker.run(-1.0)
                            ),
                            waitMs(3000)
                    ),
                    parallel(
                            follow(follower, park()),
                            Shooter.run(0, 0)
                    )
            ),
            // FALSE: Timed out (no tag seen in 15s), just park safely
            sequential(
                    parallel(
                            follow(follower, park()),
                            Shooter.run(0,0),
                            Kicker.run(0),
                            Intake.run(0)
                           // Gate.run(0),
                            //Separator.run(0)
                    )
                )
            ) //end of Conditional (if tag found)

        ); // end of main Sequential
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop(){
        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        vision.init(hardwareMap, 1, LimelightClusterTracker.RED_OPPOSITE_IDS);
        OpModeStorage.selectedPosition = OpModeStorage.StartingPosition.RED_DOWN;
        follower.setPose(start);
        follower.update();

        Intake.init(hardwareMap);
        Kicker.init(hardwareMap);
        Shooter.init(hardwareMap);
        Plow.init(hardwareMap);
    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {

    }

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        schedule(autoRoutine());
        // using the same park path from the last page
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
        // Saves the final robot position when Autonomous finishes
        OpModeStorage.autonomousEndPose = follower.pose();
        vision.stop();
    }

}
