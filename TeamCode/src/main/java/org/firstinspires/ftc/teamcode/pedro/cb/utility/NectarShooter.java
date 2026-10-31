package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class NectarShooter {

    // Static reference to the motor
    private static DcMotor nectarShooterMotor;

    /**
     * Initialize the hardware during OpMode init.
     * Assumes the configuration name in the Driver Station is "PoolenShooter".
     */
    public static void init(HardwareMap hardwareMap) {
        nectarShooterMotor = hardwareMap.get(DcMotor.class, "NectShoot");
        nectarShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double NectShooterVel) {
        return Command.build()
                .setStart(() -> {
                    ((DcMotorEx) nectarShooterMotor).setVelocity(NectShooterVel);
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                    ((DcMotorEx) nectarShooterMotor).setVelocity(NectShooterVel);
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return false;
                })
                .setEnd(interrupted -> {
                    // Safety: turns the motor off when the command is interrupted/ended
                    nectarShooterMotor.setPower(0.0);
                });
    }
}
