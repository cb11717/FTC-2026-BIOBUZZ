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
                Controller primaryTranslationalForward = Controller.proportional(0.2382378374096391);
                Controller secondaryTranslationalForward = Controller.proportional(0.08802251509060485);
                Controller primaryTranslationalLateral = Controller.proportional(0.34914796560594413);
                Controller secondaryTranslationalLateral = Controller.proportional(0.12900084388593325);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.015622915873966123));
                c.brake.set(Controller.proportionalFeedforward(0.013279478492871205));

                c.headingFeedback.set(Controller.proportional(4.074972914457705));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.031239472828169676, 0.011751254240843646));

                c.linearBrakeCoefficients.set(Matrix.diag(0.04252941017687161, 0.04768658576015959));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0027118220294830965, 0.0021697258265482274));

                c.maxAchievableForwardVelocity.set(66.06136151274606);
                c.maxAchievableStrafeVelocity.set(46.8292814283701);
                c.naturalForwardDeceleration.set(34.232522607171916);
                c.naturalStrafeDeceleration.set(64.7948846304758);
            }
    );
}

