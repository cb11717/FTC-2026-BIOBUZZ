package org.firstinspires.ftc.teamcode.pedro.cb.auto;

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

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.race;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Intake;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Plow;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;

@Autonomous(name="BlueAutoDown_new", group="Auto", preselectTeleOp="TeleOpJava")
@Disabled
public class BlueAutoDown_new extends OpMode {

    // Selection Enum
    private enum Routine {
        BLUE_AUTO_DOWN,
        BLUE_DOWN_PARK
    }
    private Routine selectedRoutine = Routine.BLUE_AUTO_DOWN; // Default fallback

    // --- BlueAutoDown Variables & Poses ---
    int TPS_Poll_Wall = 1530;
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
    private final Pose parkDown = poseFactory.of(128, 22, -90);
    private final Pose parkControl1 = poseFactory.of(80, 30, 0);
    private final Pose parkControl2 = poseFactory.of(100, 30, 0);

    // --- BlueDown_Park Poses ---
    private final Pose straightPark = poseFactory.of(128, 22, -90);
    private final Pose straightParkControl1 = poseFactory.of(76, 34, 0);

    // Path definitions
    public Path flowerPath() {
        return Paths.curve(start, flowerControl1, flower).linear(start, flower);
    }

    public Path shootPollenPath() {
        return Paths.curve(flower, shootPollenControl1, shootPollen).linear(flower, shootPollen);
    }

    public Path parkDownPath() {
        return Paths.curve(shootPollen, parkControl1, parkControl2, parkDown).linear(shootPollen, parkDown);
    }

    public Path straightParkPath() {
        return Paths.curve(start, straightParkControl1, straightPark).linear(start, straightPark);
    }

    // Command builder for BlueAutoDown
    private Command blueAutoDownRoutine() {
        return parallel(
                // Background shooter flywheels (runs for the entire match)
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                sequential(
                        // 1. Parallel: Wait at least 5.5s while running the 15s tag search
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
                                                parallel(Kicker.run(-0.7, -1.0)),
                                                waitMs(5000)
                                        ),
                                        // SAFETY: Explicitly stop kicker before driving to flower
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
                                                                follow(follower, flowerPath())
                                                        ),
                                                        // 3-second plow wiggle at the flower
                                                        race(
                                                                sequential(
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.9), waitMs(250),
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.9), waitMs(250),
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.9), waitMs(250),
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.9)
                                                                ),
                                                                waitMs(3000)
                                                        ),
                                                        // Drive back to shootPollen with plow up
                                                        parallel(
                                                                Plow.run(0),
                                                                follow(follower, shootPollenPath())
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
                                                parallel(Kicker.run(-0.7, -1.0)),
                                                waitMs(3000)
                                        ),
                                        // Stop kicker after second shot
                                        race(
                                                Kicker.run(0, 0),
                                                waitMs(50)
                                        ),
                                        // Clean park path
                                        follow(follower, parkDownPath())
                                ),
                                // FALSE: Timeout branch (just park safely)
                                sequential(
                                        follow(follower, parkDownPath())
                                )
                        )
                )
        );
    }

    // Command builder for BlueDown_Park
    private Command blueDownParkRoutine() {
        return sequential(
                follow(follower, straightParkPath())
        );
    }

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

    @Override
    public void init_loop() {
        // --- BUTTON SELECTION MENU ---
        if (gamepad1.x || gamepad1.triangle) {
            selectedRoutine = Routine.BLUE_AUTO_DOWN;
        } else if (gamepad1.square || gamepad1.y) {
            selectedRoutine = Routine.BLUE_DOWN_PARK;
        }

        telemetry.addData("=== SELECT ROUTINE ===", "");
        telemetry.addData("Press [X] or [Triangle]", "-> BlueAutoDown (Scoring + Park)");
        telemetry.addData("Press [Square] or [Y]", "-> BlueDown_Park (Direct Park)");
        telemetry.addData("----------------------", "");
        telemetry.addData("CURRENTLY SELECTED:", selectedRoutine);
        telemetry.addData("LL Total Tags Seen", vision.getTotalVisibleTagCount());
        telemetry.addData("LL Raw Tag IDs", vision.getVisibleTagIdsString());
        telemetry.addData("LL Cluster Matches", vision.getVisibleClusterTagCount());
        telemetry.update();
    }

    @Override
    public void start() {
        this.tagFound = false;
        Scheduler.reset();

        // Schedule the routine chosen during init_loop
        if (selectedRoutine == Routine.BLUE_AUTO_DOWN) {
            schedule(blueAutoDownRoutine());
        } else {
            schedule(blueDownParkRoutine());
        }
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();

        telemetry.addData("Active Routine", selectedRoutine);
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

    @Override
    public void stop() {
        OpModeStorage.autonomousEndPose = follower.pose();
        vision.stop();
    }
}