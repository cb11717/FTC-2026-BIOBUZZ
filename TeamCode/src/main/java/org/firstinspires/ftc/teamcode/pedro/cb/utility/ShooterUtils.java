package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;

public class ShooterUtils {

    /**
     * Waits for a pollen shot dip and flywheel velocity recovery back near target TPS.
     *
     * @param targetTps   The target pollen velocity (e.g., TPS_Poll_Wall = 1630)
     * @param dipOffset   How far below targetTps counts as a shot dip (e.g., 130 -> 1500 TPS)
     * @param recOffset   How close to targetTps counts as recovered (e.g., 30 -> 1600 TPS)
     * @param settleMs    Extra settle buffer in milliseconds after recovery (e.g., 120)
     */
    public static Command waitForPollenRecovery(
            double targetTps,
            double dipOffset,
            double recOffset,
            long settleMs
    ) {
        return sequential(
                //waitUntil(() -> Shooter.getPollenVelocity() < (targetTps - dipOffset)),
                waitUntil(() -> ShooterPIDF.getPollenVelocity() < (targetTps - dipOffset)),
                //waitUntil(() -> ShooterPIDF.getPollenVelocity() >= (targetTps - recOffset)),
                waitMs(settleMs)
        );
    }

    /**
     * Convenience overload with default offsets for TPS_Poll_Wall (dip -130, recover -30, settle 120ms).
     */
    public static Command waitForPollenRecovery(double targetTps) {
        return waitForPollenRecovery(targetTps, 90.0, 30.0, 300);
    }
}