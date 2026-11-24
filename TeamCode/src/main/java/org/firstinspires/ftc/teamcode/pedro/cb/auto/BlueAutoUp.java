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
import static com.pedropathing.ivy.commands.Commands.*;

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
//@Disabled // Uncomment when ready to test
public class BlueAutoUp extends OpMode {

    int TPS_Poll_Wall = 1520;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower;
    private final PoseFactory poseFactory  = PoseFactory.degrees();

    private final Pose start = poseFactory.of(80, 136, 90);
    private final Pose flower = poseFactory.of(121.5, 96, 0);
    private final Pose flowerControl1 = poseFactory.of(105, 95, 0);
    private final Pose shootPollen1 = poseFactory.of(80, 136, 90);
    private final Pose shootPollen1Control1 = poseFactory.of(120, 95, 0);
    private final Pose collectingWallPollen = poseFactory.of(127, 135, 90);
    private final Pose collectingWallPollenControl1 = poseFactory.of(125, 100, 0);
    private final Pose collectingWallPollenControl2 = poseFactory.of(135, 120, 0);
    private final Pose shootingLast4 = poseFactory.of(80, 136, 90);
    private final Pose shootingLast4Control1 = poseFactory.of(100, 110, 0);
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
        return parallel(
                // 1. Background shooter flywheels (runs for the entire match)
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),

                sequential(
                        // --- PHASE 1: INITIAL SPIN-UP WAIT & SHOOT ---
                        waitMs(5500), // Spin up shooter for 5.5 seconds
                        race(
                                parallel(
                                        Kicker.run(-0.7, -1.0)
                                ),
                                waitMs(3500)
                        ),
                        // SAFETY: Explicitly stop kicker before driving
                        race(
                                Kicker.run(0, 0),
                                waitMs(50)
                        ),

                        // --- PHASE 2 & 3: INTAKE & FLOWER COLLECTION WITH PLOW WIGGLE ---
                        race(
                                Intake.run(-1.0),
                                sequential(
                                        // Drive to flower with plow down
                                        parallel(
                                                Plow.run(0.93),
                                                follow(follower, flower())
                                        ),
                                        // 3-second plow wiggle at the flower
                                        race(
                                                sequential(
                                                        Plow.run(0.85),
                                                        waitMs(250),
                                                        Plow.run(0.93),
                                                        waitMs(250),
                                                        Plow.run(0.85),
                                                        waitMs(250),
                                                        Plow.run(0.93),
                                                        waitMs(250),
                                                        Plow.run(0.85),
                                                        waitMs(250),
                                                        Plow.run(0.93),
                                                        waitMs(250),
                                                        Plow.run(0.9),
                                                        waitMs(250),
                                                        Plow.run(0.93)
                                                ),
                                                waitMs(3000)
                                        ),
                                        // Retract plow and drive back to shooting position
                                        parallel(
                                                Plow.run(0),
                                                follow(follower, shootPollen1())
                                        )
                                )
                        ),
                        // SAFETY: Stop intake safely
                        race(
                                Intake.run(0),
                                waitMs(50)
                        ),

                        // --- PHASE 4: LIMELIGHT APRILTAG DETECTION (10s Timeout) ---
                        race(
                                waitUntil(() -> {
                                    if (vision.getVisibleClusterTagCount() > 0) {
                                        this.tagFound = true;
                                        return true; // Ends race early if tag is detected
                                    }
                                    return false;
                                }),
                                waitMs(10000) // 10-second fallback timer
                        ),

                        // --- PHASE 5: CONDITIONAL ROUTINE ---
                        conditional(
                                () -> this.tagFound,
                                // OPTION A: Tag Found within 10 seconds
                                sequential(
                                        // First shot sequence after tip
                                        race(
                                                parallel(
                                                        Kicker.run(-0.7, -1.0)
                                                ),
                                                waitMs(3000)
                                        ),
                                        // SAFETY: Stop kicker
                                        race(
                                                Kicker.run(0, 0),
                                                waitMs(50)
                                        ),
                                        follow(follower, collectingWallPollen()),
                                        // Intake collection of wall pollen
                                        race(
                                                parallel(
                                                        Intake.run(-1)
                                                ),
                                                waitMs(3000)
                                        ),
                                        // SAFETY: Stop intake
                                        race(
                                                Intake.run(0),
                                                waitMs(50)
                                        ),
                                        follow(follower, shootingLast4()),
                                        // Final shot sequence
                                        race(
                                                parallel(
                                                        Kicker.run(-0.7, -1.0)
                                                ),
                                                waitMs(3000)
                                        ),
                                        // SAFETY: Stop kicker
                                        race(
                                                Kicker.run(0, 0),
                                                waitMs(50)
                                        ),
                                        follow(follower, park())
                                ),
                                // OPTION B: Timed out -> Drive directly to park
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
        vision.init(hardwareMap, 3, LimelightClusterTracker.BLUE_OPPOSITE_IDS, telemetry);
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
        telemetry.addData("LL Total Tags Seen", vision.getTotalVisibleTagCount());
        telemetry.addData("LL Raw Tag IDs", vision.getVisibleTagIdsString());
        telemetry.addData("LL Cluster Matches", vision.getVisibleClusterTagCount());
        telemetry.update();
    }

    /** This method is called once at the start of the OpMode. **/
    @Override
    public void start() {
        this.tagFound = false;
        schedule(autoRoutine());
    }

    @Override
    public void stop() {
        OpModeStorage.autonomousEndPose = follower.pose();
        vision.stop();
    }
}