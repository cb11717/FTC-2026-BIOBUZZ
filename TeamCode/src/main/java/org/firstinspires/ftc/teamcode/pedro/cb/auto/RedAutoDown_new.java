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

import static com.pedropathing.api.Paths.*;
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

@Autonomous(name="RedAutoSelector", group="Auto", preselectTeleOp="TeleOpJava")
@Disabled
public class RedAutoDown_new extends OpMode {

    // Selection Enum
    private enum Routine {
        RED_AUTO_DOWN,
        RED_DOWN_PARK
    }
    private Routine selectedRoutine = Routine.RED_AUTO_DOWN; // Default fallback

    // --- RedAutoDown Variables & Poses ---
    int TPS_Poll_Wall = 1530;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;
    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();

    private final Pose start = p.of(59, 132, 90);
    private final Pose flower = p.of(48, 128, 90);
    private final Pose flowerControlPoint = p.of(58, 109, 0);
    private final Pose shootPollen = p.of(61, 131, 90);
    private final Pose shootPollenControlPoint = p.of(58, 109, 0);
    private final Pose parkDown = p.of(10, 117, 90);
    private final Pose parkControlPoint = p.of(60, 109, 0);

    // --- RedDown_Park Poses ---
    private final Pose straightPark = p.of(10, 117, 90);
    private final Pose straightParkControl1 = p.of(60, 109, 0);

    // Path definitions
    public Path flowerPath() {
        return curve(start, flowerControlPoint, flower).linear(start, flower);
    }

    public Path shootPollenPath() {
        return curve(flower, shootPollenControlPoint, shootPollen).linear(flower, shootPollen);
    }

    public Path parkDownPath() {
        return curve(shootPollen, parkControlPoint, parkDown).constant(parkDown);
    }

    public Path straightParkPath() {
        return Paths.curve(start, straightParkControl1, straightPark).linear(start, straightPark);
    }

    // Command builder for RedAutoDown
    private Command redAutoDownRoutine() {
        return parallel(
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
                sequential(
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
                        conditional(
                                () -> this.tagFound,
                                sequential(
                                        race(
                                                parallel(Kicker.run(-0.7, -1.0)),
                                                waitMs(5000)
                                        ),
                                        race(
                                                Kicker.run(0, 0),
                                                waitMs(50)
                                        ),
                                        race(
                                                Intake.run(-1.0),
                                                sequential(
                                                        parallel(
                                                                Plow.run(0.93),
                                                                follow(follower, flowerPath())
                                                        ),
                                                        race(
                                                                sequential(
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.93), waitMs(250),
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.93), waitMs(250),
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.93), waitMs(250),
                                                                        Plow.run(0.8), waitMs(250),
                                                                        Plow.run(0.93)
                                                                ),
                                                                waitMs(3000)
                                                        ),
                                                        parallel(
                                                                Plow.run(0),
                                                                follow(follower, shootPollenPath())
                                                        )
                                                )
                                        ),
                                        race(
                                                Intake.run(0),
                                                waitMs(50)
                                        ),
                                        race(
                                                parallel(Kicker.run(-0.7, -1.0)),
                                                waitMs(3000)
                                        ),
                                        race(
                                                Kicker.run(0, 0),
                                                waitMs(50)
                                        ),
                                        follow(follower, parkDownPath())
                                ),
                                sequential(
                                        follow(follower, parkDownPath())
                                )
                        )
                )
        );
    }

    // Command builder for RedDown_Park
    private Command redDownParkRoutine() {
        return sequential(
                follow(follower, straightParkPath())
        );
    }

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

    @Override
    public void init_loop() {
        // --- BUTTON SELECTION MENU ---
        if (gamepad1.x || gamepad1.triangle) {
            selectedRoutine = Routine.RED_AUTO_DOWN;
        } else if (gamepad1.square || gamepad1.y) {
            selectedRoutine = Routine.RED_DOWN_PARK;
        }

        telemetry.addData("=== SELECT ROUTINE ===", "");
        telemetry.addData("Press [X] or [Triangle]", "-> RedAutoDown (Scoring + Park)");
        telemetry.addData("Press [Square] or [Y]", "-> RedDown_Park (Direct Park)");
        telemetry.addData("----------------------", "");
        telemetry.addData("CURRENTLY SELECTED:", selectedRoutine);
        telemetry.update();
    }

    @Override
    public void start() {
        this.tagFound = false;
        Scheduler.reset();

        // Schedule the routine chosen during init_loop
        if (selectedRoutine == Routine.RED_AUTO_DOWN) {
            schedule(redAutoDownRoutine());
        } else {
            schedule(redDownParkRoutine());
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
        telemetry.addData("Tag Found?", this.tagFound);
        telemetry.update();
    }

    @Override
    public void stop() {
        OpModeStorage.autonomousEndPose = follower.pose();
        vision.stop();
    }
}
