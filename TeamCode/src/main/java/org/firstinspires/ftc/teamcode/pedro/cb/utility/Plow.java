package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Plow {

    private static Servo Plow;
    private static Servo PlowL;

    /**
     * Initialize the hardware during OpMode init.
     * Assumes the configuration name in the Driver Station is "PoolenShooter".
     */
    public static void init(HardwareMap hardwareMap) {

        Plow = hardwareMap.get(Servo.class, "Plow");
        PlowL = hardwareMap.get(Servo.class, "PlowL");
    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double Position) {
        return Command.build()
                .setStart(() -> {
                    Plow.setPosition(Position);
                    PlowL.setPosition(Position);
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                    Plow.setPosition(Position);
                    PlowL.setPosition(Position);
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return true;
                });
    }
}
