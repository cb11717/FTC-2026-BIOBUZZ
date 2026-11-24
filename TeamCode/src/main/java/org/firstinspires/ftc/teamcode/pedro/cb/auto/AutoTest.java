package org.firstinspires.ftc.teamcode.pedro.cb.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.race;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Gate;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Intake;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Kicker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.LimelightClusterTracker;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Plow;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Separator;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.Shooter;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.ShooterPIDF;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.ShooterUtils;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.TelemetryUtil;

import static com.pedropathing.api.Paths.*;

@Autonomous(name="AutoTest", group="Auto", preselectTeleOp="TeleOpJava")

public class AutoTest extends OpMode {

    int TPS_Poll_Wall = 1590;
    int TPS_Nect_Wall = 1900;
    private boolean tagFound = false;

    private final LimelightClusterTracker vision = new LimelightClusterTracker();

    private Follower follower; // you added this before
    private final PoseFactory p = PoseFactory.degrees(); // add this

    private final Pose start = p.of(24, 0, 90); //This is our starting point
    private final Pose end24Y = p.of(24, 24, 90);

    public Path straightLineY24() {
        return line(start, end24Y).linear(start, end24Y);
    }


    private Command autoRoutine() {


      // Shooter.setPower(0.5);
        return parallel(
                ShooterPIDF.run(TPS_Poll_Wall, 0.0),
                sequential(
                        waitMs(3000),
                        parallel(
                                Intake.run(-1.0),
                                Kicker.run(0, -1.0),
                                waitMs(4000)
                        )

                )

        );



/*
        return parallel(
           ShooterPIDF.run(TPS_Poll_Wall, 0.0),
           // Shooter.run(TPS_Poll_Wall, TPS_Nect_Wall),
             sequential(
                     waitMs(3000), // Initial spin-up

                     // Ball 1
                     parallel(
                             Intake.run(-1.0),
                             Kicker.run(0, -1.0),
                             ShooterUtils.waitForPollenRecovery(TPS_Poll_Wall)
                     ),
                     waitMs(250),
                     waitUntil(() -> ShooterPIDF.getPollenVelocity() >= (TPS_Poll_Wall - 20)),

                     // Ball 2
                     parallel(
                             Intake.run(-1.0),
                             Kicker.run(0, -1.0),
                             ShooterUtils.waitForPollenRecovery(TPS_Poll_Wall)
                     ),
                     waitMs(250),
                     waitUntil(() -> ShooterPIDF.getPollenVelocity() >= (TPS_Poll_Wall - 20)),
                     // Ball 3
                     parallel(
                             Intake.run(-1.0),
                             Kicker.run(0, -1.0),
                             ShooterUtils.waitForPollenRecovery(TPS_Poll_Wall) // slightly longer settle before ball 4
                     ),
                     waitMs(250),
                     waitUntil(() -> ShooterPIDF.getPollenVelocity() >= (TPS_Poll_Wall - 20)),
                     // Ball 4
                     parallel(
                             Intake.run(-1.0),
                             Kicker.run(0, -1.0),
                             waitMs(1500)
                     )
             )
        );

*/




        /*
        if (vision.getVisibleClusterTagCount() > 0){
            telemetry.addData("**FOUND APRIL TAG**", "");
            telemetry.update();
        }else {
            telemetry.addData("NO APRIL TAG", "");
            telemetry.update();
        }

         */


    }

    @Override
    public void loop(){

        follower.update();
        //Kicker.forcePower(-0.6);
        Scheduler.execute();
        // add your other methods needed in the loop here


/*
        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.addData("Kicker Loop Count", Kicker.loopCounter);
        telemetry.addData("Kicker Interrupted", Kicker.KickerInterrupted);

 */

        telemetry.addData("Pollen Target TPS", TPS_Poll_Wall);
        telemetry.addData("Pollen Actual TPS", ShooterPIDF.getPollenVelocity());
        telemetry.addData("Pollen Current (A)", ShooterPIDF.getPollenCurrentAmps());
        telemetry.update();
    }

    @Override
    public void init() {

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
        //Gate.init(hardwareMap);
        //Separator.init(hardwareMap);
        Intake.init(hardwareMap);
        Kicker.init(hardwareMap);
       //Shooter.init(hardwareMap);
        ShooterPIDF.init(hardwareMap);
        Plow.init(hardwareMap);

        //Blue Auto Down
        vision.init(hardwareMap, 1, LimelightClusterTracker.BLUE_AUDIENCE_IDS, telemetry);
        // --- INITIALIZE TELEMETRY UTILITY HERE ---
        TelemetryUtil.init(telemetry);


    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        schedule(autoRoutine());
        // using the same park path from the last page
    }

    @Override
    public void stop() {

    }
}