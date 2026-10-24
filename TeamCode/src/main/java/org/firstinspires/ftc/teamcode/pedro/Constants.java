package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static Follower create(HardwareMap h) {
        // return new Follower(Drivetrain, Localizer, Foresight);
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("LFdrive");
        c.frontRightName.set("RFdrive");
        c.backLeftName.set("LBdrive");
        c.backRightName.set("RBdrive");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.manualBrakeMode.set(true);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(+6.4153307817113685);
        c.yPodOffset.set(-2.7755893497016486);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.3277107642742368);
                Controller secondaryTranslationalForward = Controller.proportional(0.1210803708064366);
                Controller primaryTranslationalLateral = Controller.proportional(0.4502856429818623);
                Controller secondaryTranslationalLateral = Controller.proportional(0.1663685132278238);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018503623562718167));
                c.brake.set(Controller.proportionalFeedforward(0.015728080028310443));

                c.headingFeedback.set(Controller.proportional(5.817395306174673));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04020371662574233, 0.009612089464753308));

                c.linearBrakeCoefficients.set(Matrix.diag(0.048576861322885245, 0.03413433900309844));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.002366801236697436, 0.0025773557276820556));

                c.maxAchievableForwardVelocity.set(60.5666083280869);
                c.maxAchievableStrafeVelocity.set(44.150683970870645);
                c.naturalForwardDeceleration.set(40.6706680405879);
                c.naturalStrafeDeceleration.set(81.85607994642473);
            }
    );
}