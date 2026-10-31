package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.pedropathing.math.Pose;

public class OpModeStorage {
    // Static variable persists in memory between Auto and TeleOp
    public static Pose autonomousEndPose = new Pose(0, 0, 0);

    // Define the enum for your starting positions
    public enum StartingPosition {
        BLUE_UP,
        BLUE_DOWN,
        RED_UP,
        RED_DOWN,
        UNDEFINED
    }

    // Static variable to store the selected position (with a default value)
    public static StartingPosition selectedPosition = StartingPosition.UNDEFINED;
}
