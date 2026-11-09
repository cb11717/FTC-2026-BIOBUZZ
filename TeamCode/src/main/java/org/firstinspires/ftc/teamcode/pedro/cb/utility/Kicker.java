package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Kicker {

    private static CRServo NectKick;
    private static CRServo PllnKick;
    private static CRServo PllnKick2;
    public static int loopCounter = 0;
    public static String KickerInterrupted ="NO";


    /**
     * Initialize the hardware during OpMode init.
     * Assumes the configuration name in the Driver Station is "PoolenShooter".
     */
    public static void init(HardwareMap hardwareMap) {

        NectKick = hardwareMap.get(CRServo.class, "NectKick");
        NectKick.setDirection(CRServo.Direction.REVERSE);

        PllnKick = hardwareMap.get(CRServo.class, "PllnKick");
        PllnKick2 = hardwareMap.get(CRServo.class, "PllnKick2");

        // Force the REV Hub to initialize PWM signals on these ports during Init:
        NectKick.setPower(0.0);
        PllnKick.setPower(0.0);
        PllnKick2.setPower(0.0);

        KickerInterrupted = "NO";

    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double power) {
        return Command.build()
                .setStart(() -> {
                    NectKick.setPower(power);
                    PllnKick.setPower(power);
                    PllnKick2.setPower(power);
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                    NectKick.setPower(power);
                    PllnKick.setPower(power);
                    PllnKick2.setPower(power);
                    loopCounter++;
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return false;
                })
                .setEnd(interrupted -> {
                    // Safety: turns the motor off when the command is interrupted/ended
                   // NectKick.setPower(0);
                    //PllnKick.setPower(0);
                    KickerInterrupted = "***YES***";

                });
    }

    public static void forcePower(double power) {
        if (NectKick != null && PllnKick != null) {
            NectKick.setPower(power);
            PllnKick.setPower(power);
            PllnKick2.setPower(power);
        }
    }
}
