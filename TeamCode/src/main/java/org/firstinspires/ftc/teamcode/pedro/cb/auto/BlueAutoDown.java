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

@Autonomous(name="BlueAutoDown", group="Auto", preselectTeleOp="TeleOpJava")
public class BlueAutoDown extends OpMode {

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory poseFactory = PoseFactory.degrees(); // add this

    private final Pose start = poseFactory.of(84, 8, -90);
    private final Pose flower = poseFactory.of(95, 10, -80);
    private final Pose flowerControl1 = poseFactory.of(88, 40, 0);
   // private final Pose shootPollenStart = poseFactory.of(95, 10, -80);
    private final Pose shootPollen = poseFactory.of(84, 8, -90);
    private final Pose shootPollenControl1 = poseFactory.of(88, 40, 0);
    //private final Pose parkStart = poseFactory.of(84, 8, -90);
    private final Pose park = poseFactory.of(133, 26, -90);
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

        int clusterCount = vision.getVisibleClusterTagCount();
        return sequential(

                // Add mechanism commands here.
                follow(follower, flower()),
                // Add mechanism commands here.
                follow(follower, shootPollen()),
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
        vision.init(hardwareMap, 1, LimelightClusterTracker.BLUE_AUDIENCE_IDS);
        OpModeStorage.selectedPosition = OpModeStorage.StartingPosition.BLUE_DOWN;
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
