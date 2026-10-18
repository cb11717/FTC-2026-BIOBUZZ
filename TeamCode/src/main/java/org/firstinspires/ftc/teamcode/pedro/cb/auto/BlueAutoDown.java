package org.firstinspires.ftc.teamcode.pedro.cb.auto;

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

@Autonomous(name="BlueAutoDown", group="Auto", preselectTeleOp="TeleOpJava")
public class BlueAutoDown extends OpMode {
    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    //our poses
    private final Pose start = p.of(59, 132, 90); //This is the starting point
    private final Pose detectingAprilTag = p.of(59, 105, 90); //We go forward to detect the April Tag
    private final Pose shootingFirst4 = p.of(59, 132, 90); // Now we go back to shoot the 4
    private final Pose flower = p.of(53, 130, 110); // We spline to the flower to get 4 more
    private final Pose flowerControlPoint = p.of(58, 116, 0); // This is the control point for te previous spline
    private final Pose shootingLast4 = p.of(59, 133, 90); //We spline back to shoot those 4
    private final Pose shootingLast4ControlPoint = p.of(58, 116, 0); // This is the control point for the previous spline
    private final Pose park = p.of(9, 106, 90); //We spline to park
    private final Pose parkControlPoint = p.of(61, 104, 0); // This is the control point for the previous spline

    public Path detectingAprilTag() {
        return line(start, detectingAprilTag).linear(start, detectingAprilTag);
    }

    public Path shootingFirst4() {
        return line(detectingAprilTag, shootingFirst4).linear(detectingAprilTag, shootingFirst4);
    }

    public Path flower() {
        return curve(shootingFirst4, flowerControlPoint, flower).linear(shootingFirst4, flower);
    }

    public Path shootingLast4() {
        return curve(flower, shootingLast4ControlPoint, shootingLast4).linear(flower, shootingLast4);
    }

    public Path park() {
        return curve(shootingLast4, parkControlPoint, park).linear(shootingLast4, park);
    }


    private Command autoRoutine() {
        return sequential(
                follow(follower, detectingAprilTag()),
                // Add mechanism commands here.
                follow(follower, shootingFirst4()),
                // Add mechanism commands here.
                follow(follower, flower()),
                // Add mechanism commands here.
                follow(follower, shootingLast4()),
                // Add mechanism commands here.
                follow(follower, park())
                // Add mechanism commands here.
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

    }

}
