package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Separator {

    private static DcMotor Separator;

    /**
     * Initialize the hardware during OpMode init.
     * Assumes the configuration name in the Driver Station is "PoolenShooter".
     */
    public static void init(HardwareMap hardwareMap) {

        Separator = hardwareMap.get(DcMotor.class, "Separator");
    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double power) {
        return Command.build()
                .setStart(() -> {
                    Separator.setPower(1);
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                    Separator.setPower(1);
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return false;
                })
                .setEnd(interrupted -> {
                    // Safety: turns the motor off when the command is interrupted/ended
                    Separator.setPower(0);

                });
    }
}
