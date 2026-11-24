package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Shooter {

    // Static reference to the motor
    private static DcMotor nectarShooterMotor;
    private static DcMotor pollenShooterMotor;

    /**
     * Initialize the hardware during OpMode init.
     * Assumes the configuration name in the Driver Station is "PoolenShooter".
     */
    public static void init(HardwareMap hardwareMap) {
        nectarShooterMotor = hardwareMap.get(DcMotor.class, "NectShoot");
        nectarShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       ((DcMotorEx) nectarShooterMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pollenShooterMotor = hardwareMap.get(DcMotor.class, "PllnShoot");
        pollenShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        pollenShooterMotor.setDirection(DcMotor.Direction.REVERSE);

       ((DcMotorEx) pollenShooterMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double PollShooterVel, double NectShooterVel) {
        return Command.build()
                .setStart(() -> {
                    ((DcMotorEx) nectarShooterMotor).setVelocity(NectShooterVel);
                    ((DcMotorEx) pollenShooterMotor).setVelocity(PollShooterVel);
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                   ((DcMotorEx) nectarShooterMotor).setVelocity(NectShooterVel);
                    ((DcMotorEx) pollenShooterMotor).setVelocity(PollShooterVel);
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return false;
                })
                .setEnd(interrupted -> {
                    // Safety: turns the motor off when the command is interrupted/ended
                    nectarShooterMotor.setPower(0.0);
                    pollenShooterMotor.setPower(0.0);
                });
    }

    public static double getPollenVelocity() {
        if (pollenShooterMotor instanceof DcMotorEx) {
            return ((DcMotorEx) pollenShooterMotor).getVelocity();
        }
        return 0.0;
    }

    public static double getPollenCurrentAmps() {
        if (pollenShooterMotor instanceof DcMotorEx) {
            return ((DcMotorEx) pollenShooterMotor).getCurrent(CurrentUnit.AMPS);
        }
        return 0.0;
    }

    //used only for test purpose to tune the PIDF
    public static void setPower(double power) {
        if (pollenShooterMotor instanceof DcMotorEx) {
            pollenShooterMotor.setPower(power);
        }
        return;
    }

}
