package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PollenShooter {

    // Static reference to the motor
    private static DcMotor pollenShooterMotor;

    /**
     * Initialize the hardware during OpMode init.
     * Assumes the configuration name in the Driver Station is "PoolenShooter".
     */
    public static void init(HardwareMap hardwareMap) {
        pollenShooterMotor = hardwareMap.get(DcMotor.class, "PllnShoot");
        pollenShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        pollenShooterMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double PollShooterVel) {
        return Command.build()
                .setStart(() -> {
                    ((DcMotorEx) pollenShooterMotor).setVelocity(PollShooterVel);
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                    ((DcMotorEx) pollenShooterMotor).setVelocity(PollShooterVel);
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return false;
                })
                .setEnd(interrupted -> {
                    // Safety: turns the motor off when the command is interrupted/ended
                    pollenShooterMotor.setPower(0.0);
                });
    }
}
