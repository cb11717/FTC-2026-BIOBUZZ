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

@Autonomous(name="RedAutoUp", group="Auto", preselectTeleOp="TeleOpJava")
@Disabled
public class RedAutoUp extends OpMode {

    int TPS_Poll_Wall = 1500;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    //our poses
    private final Pose start = p.of(56, 9, -90); //This is our starting point

    private final Pose flower = p.of(15, 50, -180); //Here we go to the flower to get 3 more pollem as we have 1 leftover
    private final Pose shootingAfterTip = p.of(56, 9, -90); //Here we go back to shoot all 4 after the hive tips
    private final Pose shootingAfterTipControlPoint = p.of(39, 38, 0);
    private final Pose liningUp = p.of(9, 29, -90); //Here we line up with the wall and spline
    private final Pose liningUpControlPoint = p.of(11.8, 48, 0); //This is the control point of the previous spline
    private final Pose collectingWallPollen = p.of(9, 10, -90); //Now we rush forward to collect all the
    private final Pose shootingLast4 = p.of(56, 9, -90); //Here we spline back to shoot the last 4
    private final Pose shootingLast4ControlPoint = p.of(11.8, 48, 0); //This is control point of the previous spline
    private final Pose Park = p.of(12, 92, -90);
    private final Pose ParkControl1 = p.of(18, 8, 0);
  /*  public Path shootingFirst3() {
        return line(stay, shootingFirst3).linear(stay, shootingFirst3);
    }

   */

    // Short path just to clear the wall
    public Path clearWall() {
        return line(start, p.of(56, 15, -90)).linear(start, p.of(56, 15, -90));
    }

    // The rest of the path to the flower
    public Path toFlower() {
        return line(p.of(56, 15, -90), flower).linear(p.of(56, 15, -90), flower);
    }

    public Path flower() {
        return line(start, flower).linear(start, flower);
    }

    public Path shootingAfterTip() {
        return curve(flower, shootingAfterTipControlPoint,shootingAfterTip).linear(flower, shootingAfterTip);
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
                // =========================================================================
                // 1. BACKGROUND TASK: Shooter stays running continuously the ENTIRE routine
                // =========================================================================
                Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),

                // =========================================================================
                // 2. MAIN SEQUENTIAL EXECUTION
                // =========================================================================
                sequential(
                        // --- PHASE 1: INITIAL SHOOT (3.5 seconds total) ---
                        // Shooter is already running; kicker waits 500ms for spin-up before firing
                        race(
                                sequential(
                                        waitMs(3000),
                                        Kicker.run(-1.0)
                                ),
                                waitMs(6000)
                        ),

                        // --- PHASE 2: SAFE WALL CLEARANCE & SLOW PLOW DEPLOYMENT ---
                        // Step A: Drive off the wall AND start opening the plow concurrently in open space
                        parallel(
                                follow(follower, clearWall()),
                                sequential(
                                        waitMs(50),      // Tiny initial delay so the robot safely clears the wall first
                                        Plow.run(1)       // Triggers the plow to open while moving
                                )
                        ),

                        // Step B: Give the slow servo extra buffer time to finish its full swing safely
                        waitMs(1700),                      // Increase to 1000ms if your servo is extra slow

                        // Step C: Now drive fast to the flower with the plow guaranteed to be fully open
                        // Drive to flower with a safety timeout (prevents hanging if it stalls slightly short)
                        race(
                                follow(follower, flower()),
                                waitMs(4000) // Forces progression to intake after 4 seconds max
                        ),

                        // --- PHASE 3: INTAKE AT FLOWER ---
                        race(
                                parallel(
                                        Intake.run(-1)
                                ),
                                waitMs(3000)
                        ),

                        // --- PHASE 4: RETRACT PLOW & DRIVE TO SHOOTING POSITION ---
                        Plow.run(0),
                        race(
                                follow(follower, shootingAfterTip()),
                                waitMs(4000)
                        ),

                        // --- PHASE 5: LIMELIGHT APRILTAG DETECTION (15s Timeout) ---
                        race(
                                waitUntil(() -> {
                                    if (vision.getVisibleClusterTagCount() > 0) {
                                        this.tagFound = true;
                                        return true; // Ends race early if tag is detected
                                    }
                                    return false;
                                }),
                                waitMs(15000) // 15-second fallback timer
                        ),

                        // --- PHASE 6: CONDITIONAL ROUTINE ---
                        conditional(
                                () -> this.tagFound,
                                // OPTION A: Tag Found within 15 seconds -> Shoot, intake wall pollen, shoot, and park
                                sequential(
                                        // Shooter is already running at speed; fire kicker
                                        race(
                                                Kicker.run(-1.0),
                                                waitMs(3000)
                                        ),
                                        race(
                                                follow(follower, liningUp()),
                                                waitMs(4000)
                                        ),
                                        race(
                                                follow(follower, collectingWallPollen()),
                                                waitMs(4000)
                                        ),
                                        race(
                                                parallel(
                                                        Intake.run(-1)
                                                ),
                                                waitMs(3000)
                                        ),
                                        race(
                                                follow(follower, shootingLast4()),
                                                waitMs(4000)
                                        ),
                                        // Final shot
                                        race(
                                                Kicker.run(-1.0),
                                                waitMs(3000)
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
        vision.init(hardwareMap, 1, LimelightClusterTracker.RED_AUDIENCE_IDS);
        OpModeStorage.selectedPosition = OpModeStorage.StartingPosition.RED_UP;
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
