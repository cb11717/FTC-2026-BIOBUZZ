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

@Autonomous(name="RedAutoUp", group="Auto", preselectTeleOp="TeleOpJava")
public class RedAutoUp extends OpMode {
    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    //our poses
    private final Pose start = p.of(56, 9, -90); //This is our starting point
    //private final Pose stay = p.of(56, 9, -90); //We want to stay here because we will shoot here next
    //private final Pose shootingFirst3 = p.of(56, 9, -90);//We shoot the first 3 pollen
    private final Pose flower = p.of(11.7, 60, -160); //Here we go to the flower to get 3 more pollem as we have 1 leftover
    private final Pose shootingAfterTip = p.of(56, 9, -90); //Here we go back to shoot all 4 after the hive tips
    private final Pose liningUp = p.of(9, 29, -90); //Here we line up with the wall and spline
    private final Pose liningUpControlPoint = p.of(11.8, 48, 0); //This is the control point of the previous spline
    private final Pose collectingWallPollen = p.of(9, 9, -90); //Now we rush forward to collect all the
    private final Pose shootingLast4 = p.of(56, 9, -90); //Here we spline back to shoot the last 4
    private final Pose shootingLast4ControlPoint = p.of(11.8, 48, 0); //This is control point of the previous spline

  /*  public Path shootingFirst3() {
        return line(stay, shootingFirst3).linear(stay, shootingFirst3);
    }

   */

    public Path flower() {
        return line(start, flower).linear(start, flower);
    }

    public Path shootingAfterTip() {
        return line(flower, shootingAfterTip).linear(flower, shootingAfterTip);
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


    private Command autoRoutine() {
        return sequential(

                // Add mechanism commands here.
                follow(follower, flower()),
                // Add mechanism commands here.
                follow(follower, shootingAfterTip()),
                // Add mechanism commands here.
                follow(follower, liningUp()),
                // Add mechanism commands here.
                follow(follower, collectingWallPollen()),
                // Add mechanism commands here.
                follow(follower, shootingLast4())
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
