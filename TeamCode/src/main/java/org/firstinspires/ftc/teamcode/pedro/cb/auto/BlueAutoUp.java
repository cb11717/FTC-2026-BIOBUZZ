package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import com.pedropathing.api.Paths;
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
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.race;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.pedropathing.ivy.Command;

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

@Autonomous(name="BlueAutoUp", group="Auto", preselectTeleOp="TeleOpJava")
@Disabled
public class BlueAutoUp extends OpMode {

    int TPS_Poll_Wall = 1500;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory poseFactory  = PoseFactory.degrees(); // add this

    private final Pose start = poseFactory.of(80, 136, 90);
    private final Pose flower = poseFactory.of(130, 96, 0);
    private final Pose flowerControl1 = poseFactory.of(120, 95, 0);
    //private final Pose shootPollen1Start = poseFactory.of(130, 90, 20);
    private final Pose shootPollen1 = poseFactory.of(80, 136, 90);
    private final Pose shootPollen1Control1 = poseFactory.of(120, 95, 0);
    //private final Pose collectingWallPollenStart = poseFactory.of(80, 136, 90);
    private final Pose collectingWallPollen = poseFactory.of(135, 136, 90);
    private final Pose collectingWallPollenControl1 = poseFactory.of(125, 100, 0);
    private final Pose collectingWallPollenControl2 = poseFactory.of(135, 120, 0);
    //private final Pose shootingLast4Start = poseFactory.of(135, 136, 90);
    private final Pose shootingLast4 = poseFactory.of(80, 136, 90);
    private final Pose shootingLast4Control1 = poseFactory.of(100, 110, 0);
    //private final Pose parkStart = poseFactory.of(80, 136, 90);
    private final Pose park = poseFactory.of(130, 50, 90);
    private final Pose parkControl1 = poseFactory.of(160, 110, 0);
    private final Pose parkControl2 = poseFactory.of(95, 80, 0);

    public Path flower() {
        return Paths.curve(start, flowerControl1, flower).linear(start, flower);
    }

    public Path shootPollen1() {
        return Paths.curve(flower, shootPollen1Control1, shootPollen1).linear(flower, shootPollen1);
    }

    public Path collectingWallPollen() {
        return Paths.curve(shootPollen1, collectingWallPollenControl1, collectingWallPollenControl2, collectingWallPollen).linear(shootPollen1, collectingWallPollen);
    }

    public Path shootingLast4() {
        return Paths.curve(collectingWallPollen, shootingLast4Control1, shootingLast4).linear(collectingWallPollen, shootingLast4);
    }

    public Path park() {
        return Paths.curve(shootingLast4, parkControl1, parkControl2, park).linear(shootingLast4, park);
    }

    private Command autoRoutine() {

        int clusterCount = vision.getVisibleClusterTagCount();
        return sequential(

                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                sequential(
                        waitMs(500),           // Spin up shooter for 0.5 seconds
                        Kicker.run(-1.0)       // Then start the kicker and keep it running
                ),
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
                //pick from flower
                race(
                        parallel(
                                Intake.run(1)
                               // Gate.run(1),
                                //Separator.run(1)
                        ),
                        waitMs(3000)
                ),

                parallel(
                        Plow.run(0),
                        follow(follower, shootPollen1())
                    ),

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
                        sequential(
                                race(
                                        parallel(
                                                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                                                Kicker.run(-1.0)
                                        ),
                                        waitMs(3000)
                                ),
                                follow(follower, collectingWallPollen()),
                                race(
                                        parallel(
                                                Intake.run(0.75)
                                                //Gate.run(1),
                                                //Separator.run(1)
                                        ),
                                        waitMs(3000)
                                ),
                                follow(follower, shootingLast4()),
                                race(
                                        parallel(
                                                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                                                Kicker.run(-1.0)
                                        ),
                                        waitMs(3000)
                                ),
                                follow(follower, park())
                        ),
                        // FALSE: Timed out (no tag seen in 15s), just park safely
                        sequential(
                                parallel(
                                        follow(follower, park()),
                                        Shooter.run(0,0),
                                        Kicker.run(0),
                                        Intake.run(0)
                                        //Gate.run(0),
                                       // Separator.run(0)
                                )
                        )
                )

        );

    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
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

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        vision.init(hardwareMap, 1, LimelightClusterTracker.BLUE_OPPOSITE_IDS);
        OpModeStorage.selectedPosition = OpModeStorage.StartingPosition.BLUE_UP;
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
