package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;

public class ShooterPIDF {

    private static DcMotor nectarShooterMotor;
    private static DcMotor pollenShooterMotor;

    // Tune each controller independently on your robot:
    // kF ≈ (steady motor power) / (steady TPS at that power)
    private static final VelocityPIDF pollenPid = new VelocityPIDF(
            0.0009,  // kP
            0.00007,  // kI
            0, // kD
            0.00036, // kF
            250.0   // integralMax
    );

    private static final VelocityPIDF nectarPid = new VelocityPIDF(
            0.0004,  // kP
            0.0001,  // kI
            0.00001, // kD
            0.00032, // kF
            5000.0   // integralMax
    );

    public static void init(HardwareMap hardwareMap) {
        nectarShooterMotor = hardwareMap.get(DcMotor.class, "NectShoot");
        nectarShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       /* if (nectarShooterMotor instanceof DcMotorEx) {
            ((DcMotorEx) nectarShooterMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        */

        pollenShooterMotor = hardwareMap.get(DcMotor.class, "PllnShoot");
        pollenShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        pollenShooterMotor.setDirection(DcMotor.Direction.REVERSE);
       /* if (pollenShooterMotor instanceof DcMotorEx) {
            ((DcMotorEx) pollenShooterMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        */

        pollenPid.reset();
        nectarPid.reset();
    }

    /**
     * Updates target velocities and applies one PIDF step for both shooters.
     * Passing 0 for a target stops that motor and resets its PIDF state.
     */
    public static void updateTargets(double pollTargetTps, double nectTargetTps) {
        applyMotorPidf(pollenShooterMotor, pollenPid, pollTargetTps);
        applyMotorPidf(nectarShooterMotor, nectarPid, nectTargetTps);
    }

    private static void applyMotorPidf(DcMotor motor, VelocityPIDF pid, double targetTps) {
        if (motor == null) {
            return;
        }
        if (Math.abs(targetTps) < 1e-6) {
            motor.setPower(0.0);
            pid.reset();
            return;
        }

        pid.setTargetVelocity(targetTps);
        if (motor instanceof DcMotorEx) {
            double currentTps = ((DcMotorEx) motor).getVelocity();
            motor.setPower(pid.update(currentTps));
        }
    }

    /**
     * Continuous Ivy command for Autonomous (or Ivy-driven routines).
     */
    public static Command run(double PollShooterVel, double NectShooterVel) {
        return Command.build()
                .setStart(() -> {
                    pollenPid.reset();
                    nectarPid.reset();
                    updateTargets(PollShooterVel, NectShooterVel);
                })
                .setExecute(() -> updateTargets(PollShooterVel, NectShooterVel))
                .setDone(() -> false)
                .setEnd(interrupted -> stopAll());
    }

    public static void stopAll() {
        if (pollenShooterMotor != null) {
            pollenShooterMotor.setPower(0.0);
        }
        if (nectarShooterMotor != null) {
            nectarShooterMotor.setPower(0.0);
        }
        pollenPid.reset();
        nectarPid.reset();
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

    public static double getNectarVelocity() {
        if (nectarShooterMotor instanceof DcMotorEx) {
            return ((DcMotorEx) nectarShooterMotor).getVelocity();
        }
        return 0.0;
    }

    public static double getNectarCurrentAmps() {
        if (nectarShooterMotor instanceof DcMotorEx) {
            return ((DcMotorEx) nectarShooterMotor).getCurrent(CurrentUnit.AMPS);
        }
        return 0.0;
    }
}
