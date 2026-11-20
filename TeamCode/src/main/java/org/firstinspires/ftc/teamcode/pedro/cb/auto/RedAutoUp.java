package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

@Autonomous(name="RedAutoUp", group="Auto", preselectTeleOp="TeleOpJava")
@Disabled // Uncomment when ready to test
public class RedAutoUp extends OpMode {

    int TPS_Poll_Wall = 1520;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();

    // Our poses
    private final Pose start = p.of(56, 9, -90);
    private final Pose flower = p.of(15, 50, -180);
    private final Pose shootingAfterTip = p.of(56, 9, -90);
    private final Pose shootingAfterTipControlPoint = p.of(39, 38, 0);
    private final Pose liningUp = p.of(9, 29, -90);
    private final Pose liningUpControlPoint = p.of(11.8, 48, 0);
    private final Pose collectingWallPollen = p.of(10, 10, -90);
    private final Pose shootingLast4 = p.of(56, 9, -90);
    private final Pose shootingLast4ControlPoint = p.of(11.8, 48, 0);
    private final Pose Park = p.of(12, 92, -90);
    private final Pose ParkControl1 = p.of(18, 8, 0);

    // Single direct path to the flower
    public Path flower() {
        return line(start, flower).linear(start, flower);
    }

    public Path shootingAfterTip() {
        return curve(flower, shootingAfterTipControlPoint, shootingAfterTip).linear(flower, shootingAfterTip);
    }

    public Path liningUp() {
        return curve(shootingAfterTip, liningUpControlPoint, liningUp).linear(shootingAfterTip, liningUp);
    }

    public Path collectingWallPollen() {
        return line(liningUp, collectingWallPollen).linear(liningUp, collectingWallPollen);
    }

    public Path shootingLast4() {
        return curve(collectingWallPollen, shootingLast4ControlPoint, shootingLast4).linear(collectingWallPollen, shootingLast4);
    }

    public Path park() {
        return Paths.curve(shootingLast4, ParkControl1, Park).linear(shootingLast4, Park);
    }

    private Command autoRoutine() {
        return parallel(
                // 1. Background shooter flywheels (runs for the entire match)
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),

                sequential(
                        // --- PHASE 1: INITIAL SPIN-UP WAIT & SHOOT ---
                        waitMs(5500), // Wait for flywheels to reach target speed before firing
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

                        // --- PHASE 2 & 3: INTAKE & FLOWER COLLECTION WITH PLOW WIGGLE & RETURN ---
                        race(
                                Intake.run(-1.0),
                                sequential(
                                        // Drive directly to flower with plow down
                                        parallel(
                                                Plow.run(0.93),
                                                follow(follower, flower())
                                        ),
                                        // 3-second plow wiggle at the flower
                                        race(
                                                sequential(
                                                        Plow.run(0.8),
                                                        waitMs(250),
                                                        Plow.run(0.93),
                                                        waitMs(250),
                                                        Plow.run(0.8),
                                                        waitMs(250),
                                                        Plow.run(0.93),
                                                        waitMs(250),
                                                        Plow.run(0.8),
                                                        waitMs(250),
                                                        Plow.run(0.93),
                                                        waitMs(250),
                                                        Plow.run(0.8),
                                                        waitMs(250),
                                                        Plow.run(0.93)
                                                ),
                                                waitMs(3000)
                                        ),
                                        // Retract plow and drive back to shooting position (shootingAfterTip)
                                        parallel(
                                                Plow.run(0),
                                                follow(follower, shootingAfterTip())
                                        )
                                )
                        ),
                        // SAFETY: Stop intake safely
                        race(
                                Intake.run(0),
                                waitMs(50)
                        ),

                        // --- PHASE 4: LIMELIGHT APRILTAG DETECTION (10s Timeout) AT SHOOTING POSITION ---
                        race(
                                waitUntil(() -> {
                                    if (vision.getVisibleClusterTagCount() > 0) {
                                        this.tagFound = true;
                                        return true;
                                    }
                                    return false;
                                }),
                                waitMs(10000)
                        ),

                        // --- PHASE 5: CONDITIONAL ROUTINE (SHOOT ONLY IF TAG FOUND) ---
                        conditional(
                                () -> this.tagFound,
                                // OPTION A: Tag Found within 10 seconds -> Shoot, collect wall pollen, shoot last 4, park
                                sequential(
                                        // First shot sequence after tip (ONLY fires because tag was found)
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
                                        follow(follower, liningUp()),
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
                                // OPTION B: Timed out / No tag -> Skip shooting and drive directly to park
                                sequential(
                                        follow(follower, park())
                                )
                        )
                )
        );
    }

    /** Main loop of the OpMode, runs repeatedly after clicking "Play". **/
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

    /** Called once at initialization. **/
    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        vision.init(hardwareMap, 1, LimelightClusterTracker.RED_AUDIENCE_IDS, telemetry);
        OpModeStorage.selectedPosition = OpModeStorage.StartingPosition.RED_UP;
        follower.setPose(start);
        follower.update();

        Intake.init(hardwareMap);
        Kicker.init(hardwareMap);
        Shooter.init(hardwareMap);
        Plow.init(hardwareMap);
    }

    /** Called continuously after init while waiting for "play". **/
    @Override
    public void init_loop() {
        telemetry.addData("LL Total Tags Seen", vision.getTotalVisibleTagCount());
        telemetry.addData("LL Raw Tag IDs", vision.getVisibleTagIdsString());
        telemetry.addData("LL Cluster Matches", vision.getVisibleClusterTagCount());
        telemetry.update();
    }

    /** Called once at the start of the OpMode. **/
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