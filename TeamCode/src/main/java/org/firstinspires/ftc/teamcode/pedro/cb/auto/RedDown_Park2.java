package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.race;
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

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Intake;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Plow;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;

@Autonomous(name="RedDown_Park2", group="Auto", preselectTeleOp="TeleOpJava")

public class RedDown_Park2 extends OpMode {

    int TPS_Poll_Wall = 1500;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    //our poses
    private final Pose start = p.of(59, 132, 90);
    private final Pose flower = p.of(48, 128, 90);
    private final Pose flowerControlPoint = p.of(58, 109, 0);
    private final Pose shootPollen = p.of(61, 131, 90);
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
        return parallel(
                // Background shooter flywheels (runs for the entire match)
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                sequential(
                        // 1. Parallel: Wait at least 5s while running the 15s tag search
                        parallel(
                                waitMs(5500),
                                race(
                                        waitUntil(() -> {
                                            if (vision.getVisibleClusterTagCount() > 0) {
                                                this.tagFound = true;
                                                return true;
                                            }
                                            return false;
                                        }),
                                        waitMs(15000)
                                )
                        ),
                        // 2. Conditional: Run scoring routine if tag found, else park
                        conditional(
                                () -> this.tagFound,
                                sequential(
                                        // First shot sequence (5 seconds)
                                        race(
                                                parallel(
                                                        Kicker.run(-0.7, -1.0)
                                                ),
                                                waitMs(5000)
                                        ),
                                        // SAFETY: Explicitly stop kicker before driving to flower
                                        race(
                                                Kicker.run(0, 0),
                                                Intake.run(-1.0),
                                                waitMs(50)
                                        ),

                                        // Clean park path
                                        follow(follower, park())
                                ),
                                // FALSE: Timeout branch (just park safely)
                                sequential(
                                        follow(follower, park())
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
        telemetry.addData("LL Total Tags Seen", vision.getTotalVisibleTagCount());
        telemetry.addData("LL Raw Tag IDs", vision.getVisibleTagIdsString());
        telemetry.addData("LL Cluster Matches", vision.getVisibleClusterTagCount());
        telemetry.addData("Tag Found?", this.tagFound);
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        vision.init(hardwareMap, 0, LimelightClusterTracker.RED_OPPOSITE_IDS, telemetry);
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
        telemetry.addData("LL Total Tags Seen", vision.getTotalVisibleTagCount());
        telemetry.addData("LL Raw Tag IDs", vision.getVisibleTagIdsString());
        telemetry.addData("LL Cluster Matches", vision.getVisibleClusterTagCount());
        telemetry.update();
    }

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        this.tagFound = false;
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
