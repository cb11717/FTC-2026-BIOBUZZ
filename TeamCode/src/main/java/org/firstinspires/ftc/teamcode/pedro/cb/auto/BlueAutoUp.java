package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import com.pedropathing.api.Paths;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import static com.pedropathing.api.Paths.*;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.pedropathing.ivy.Command;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.OpModeStorage;

@Autonomous(name="BlueAutoUp", group="Auto", preselectTeleOp="TeleOpJava")
public class BlueAutoUp extends OpMode {

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory poseFactory  = PoseFactory.degrees(); // add this

    private final Pose start = poseFactory.of(80, 136, 90);
    private final Pose flower = poseFactory.of(130, 90, 20);
    private final Pose flowerControl1 = poseFactory.of(120, 80, 0);
    //private final Pose shootPollen1Start = poseFactory.of(130, 90, 20);
    private final Pose shootPollen1 = poseFactory.of(80, 136, 90);
    private final Pose shootPollen1Control1 = poseFactory.of(120, 80, 0);
    //private final Pose collectingWallPollenStart = poseFactory.of(80, 136, 90);
    private final Pose collectingWallPollen = poseFactory.of(135, 136, 90);
    private final Pose collectingWallPollenControl1 = poseFactory.of(125, 100, 0);
    private final Pose collectingWallPollenControl2 = poseFactory.of(135, 120, 0);
    //private final Pose shootingLast4Start = poseFactory.of(135, 136, 90);
    private final Pose shootingLast4 = poseFactory.of(80, 136, 90);
    private final Pose shootingLast4Control1 = poseFactory.of(100, 110, 0);
    //private final Pose parkStart = poseFactory.of(80, 136, 90);
    private final Pose park = poseFactory.of(133, 48, 90);
    private final Pose parkControl1 = poseFactory.of(140, 50, 0);

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
        return Paths.curve(shootingLast4, parkControl1, park).linear(shootingLast4, park);
    }

    private Command autoRoutine() {

        int clusterCount = vision.getVisibleClusterTagCount();
        return sequential(

                // Add mechanism commands here.
                follow(follower, flower()),
                // Add mechanism commands here.
                follow(follower, shootPollen1()),
               // Add mechanism commands here.
                follow(follower, collectingWallPollen()),
                // Add mechanism commands here.
                follow(follower, shootingLast4()),
                // Add mechanism commands here.
                follow(follower, park())
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
