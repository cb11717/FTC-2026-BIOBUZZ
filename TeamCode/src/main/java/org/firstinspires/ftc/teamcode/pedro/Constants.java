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
        c.name.set("Pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-7.3228);
        c.yPodOffset.set(+0.5314);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.228525897154759);
                Controller secondaryTranslationalForward = Controller.proportional(0.08443421267424969);
                Controller primaryTranslationalLateral = Controller.proportional(0.2416240310565834);
                Controller secondaryTranslationalLateral = Controller.proportional(0.08927362316239013);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.015204569625919182));
                c.brake.set(Controller.proportionalFeedforward(0.012923884182031305));

                c.headingFeedback.set(Controller.proportional(3.337671043268673));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.032432605417266155, 0.011744209059993174));

                c.linearBrakeCoefficients.set(Matrix.diag(0.058174918062719995, 0.015020170713629611));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0016301714826633098, 0.0029276278173578757));

                c.maxAchievableForwardVelocity.set(68.16122907889307);
                c.maxAchievableStrafeVelocity.set(52.65357571899542);
                c.naturalForwardDeceleration.set(42.16794248441946);
                c.naturalStrafeDeceleration.set(69.18319566943158);
            }
    );

   /* public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(+6.4153307817113685);
        c.yPodOffset.set(-2.7755893497016486);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    */

}