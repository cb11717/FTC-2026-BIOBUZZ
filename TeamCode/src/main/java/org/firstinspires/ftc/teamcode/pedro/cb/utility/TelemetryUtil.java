package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.ivy.Command;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TelemetryUtil {

    private static Telemetry telemetryInstance;

    /**
     * Initialize the telemetry utility during OpMode init.
     */
    public static void init(Telemetry telemetry) {
        telemetryInstance = telemetry;
    }

    /**
     * Returns an Ivy Command that logs a message once and finishes instantly,
     * allowing your sequential command flow to continue seamlessly.
     */
    public static Command log(String caption, Object value) {
        return Command.build()
                .setStart(() -> {
                    if (telemetryInstance != null) {
                        telemetryInstance.addData(caption, value);
                        telemetryInstance.update();
                    }
                })
                .setDone(() -> true); // Finishes instantly so the sequence moves forward
    }
}