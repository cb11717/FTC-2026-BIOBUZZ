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

        PllnKick = hardwareMap.get(CRServo.class, "PllnKick");
        PllnKick2 = hardwareMap.get(CRServo.class, "PllnKick2");
        PllnKick.setDirection(CRServo.Direction.REVERSE);
        PllnKick2.setDirection(CRServo.Direction.REVERSE);


        // Force the REV Hub to initialize PWM signals on these ports during Init:
        NectKick.setPower(0.0);
        PllnKick.setPower(0.0);
        PllnKick2.setPower(0.0);
        KickerInterrupted = "***YES***";

    }

    /**
     * Reusable command factory to spin the PoolenShooter at a target power.
     * Runs continuously until explicitly cancelled or replaced.
     */
    public static Command run(double NectPower, double PollPower) {
        return Command.build()
                .setStart(() -> {
                    NectKick.setPower(NectPower);
                    PllnKick2.setPower(PollPower);
                    PllnKick.setPower(0.5 * PllnKick2.getPower());
                })
                .setExecute(() -> {
                    // Keep refreshing the power during scheduler loops
                    NectKick.setPower(NectPower);
                    PllnKick2.setPower(PollPower);
                    PllnKick.setPower(0.5 * PllnKick2.getPower());
                    loopCounter++;
                })
                .setDone(() -> {
                    // Returns false so it never finishes on its own (continuous mechanism)
                    return false;
                })
                .setEnd(interrupted -> {
                    // Safety: turns the motor off when the command is interrupted/ended
                   // NectKick.setPower(0.0);
                    //PllnKick.setPower(0.0);
                    //PllnKick2.setPower(0.0);
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
