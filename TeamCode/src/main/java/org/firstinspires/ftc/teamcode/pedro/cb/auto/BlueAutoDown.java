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

@Autonomous(name="BlueAutoDown", group="Auto", preselectTeleOp="CB112TWOCon10nov261748")

public class BlueAutoDown extends OpMode {

    int TPS_Poll_Wall = 1510;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(84, 8, -90);
    private final Pose flower = poseFactory.of(95, 12, -90);
    private final Pose flowerControl1 = poseFactory.of(88, 40, 0);
    private final Pose shootPollen = poseFactory.of(81, 9, -90);
    private final Pose shootPollenControl1 = poseFactory.of(88, 40, 0);
    private final Pose park = poseFactory.of(128, 22, -90);
    private final Pose parkControl1 = poseFactory.of(80, 30, 0);
    private final Pose parkControl2 = poseFactory.of(100, 30, 0);

    public Path flower() {
        return Paths.curve(start, flowerControl1, flower).linear(start, flower);
    }

    public Path shootPollen() {
        return Paths.curve(flower, shootPollenControl1, shootPollen).linear(flower, shootPollen);
    }

    public Path park() {
        return Paths.curve(shootPollen, parkControl1, parkControl2, park).linear(shootPollen, park);
    }

    private Command autoRoutine() {
        return parallel(
                // Background shooter flywheels (runs for the entire match)
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                sequential(
                        // 1. Parallel: Wait at least 7.5s while running the 15s tag search
                        parallel(
                                waitMs(7500),
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
                                        // SAFETY: Ensure kicker is explicitly stopped before driving to flower
                                        race(
                                                Kicker.run(0, 0),
                                                waitMs(50)
                                        ),
                                        // Intake & Flower collection phase
                                        race(
                                                Intake.run(-1),
                                                sequential(
                                                        // Drive to flower with plow down
                                                        parallel(
                                                                Plow.run(0.9),
                                                                follow(follower, flower())
                                                        ),
                                                        // 3-second plow wiggle at the flower
                                                        race(
                                                                sequential(
                                                                        Plow.run(0.8),
                                                                        waitMs(250),
                                                                        Plow.run(0.9),
                                                                        waitMs(250),
                                                                        Plow.run(0.8),
                                                                        waitMs(250),
                                                                        Plow.run(0.9),
                                                                        waitMs(250),
                                                                        Plow.run(0.8),
                                                                        waitMs(250),
                                                                        Plow.run(0.9),
                                                                        waitMs(250),
                                                                        Plow.run(0.8),
                                                                        Plow.run(0.9)
                                                                ),
                                                                waitMs(3000)
                                                        ),
                                                        // Drive back to shootPollen with plow up
                                                        parallel(
                                                                Plow.run(0),
                                                                follow(follower, shootPollen())
                                                        )
                                                )
                                        ),
                                        // Stop intake safely without blocking the sequence
                                        race(
                                                Intake.run(0),
                                                waitMs(50)
                                        ),
                                        // Second shot sequence (Starts ONLY after arriving at shootPollen)
                                        race(
                                                parallel(
                                                        Kicker.run(-0.7, -1.0)
                                                ),
                                                waitMs(3000)
                                        ),
                                        // Stop kicker after second shot
                                        race(
                                                Kicker.run(0, 0),
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
        vision.init(hardwareMap, 2, LimelightClusterTracker.BLUE_AUDIENCE_IDS, telemetry);
        OpModeStorage.selectedPosition = OpModeStorage.StartingPosition.BLUE_DOWN;
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

    /** This method is called once at the start of the OpMode. **/
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
        OpModeStorage.autonomousEndPose = follower.pose();
        vision.stop();
    }
}