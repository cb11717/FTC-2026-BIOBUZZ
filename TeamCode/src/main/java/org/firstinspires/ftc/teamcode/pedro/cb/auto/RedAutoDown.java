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

@Autonomous(name="RedAutoDown", group="Auto", preselectTeleOp="TeleOpJava")
public class RedAutoDown extends OpMode {
    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    //our poses
    private final Pose start = p.of(59, 132, 90);
    private final Pose flower = p.of(53, 130, 120);
    private final Pose flowerControlPoint = p.of(58, 109, 0);
    private final Pose shootPollen = p.of(60, 132, 90);
    private final Pose shootPollenControlPoint = p.of(58, 109, 0);
    private final Pose park = p.of(9, 112, 90);
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
