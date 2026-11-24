package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.pedro.cb.utility.TelemetryUtil;

import java.util.List;

public class LimelightClusterTracker {

    private static final double METERS_TO_INCHES = 39.3700787;

    private Limelight3A limelight;
    private int[] activeClusterIds;
    private int activePipelineIndex = 0;

    private double lastValidDistanceInches = -1.0;
    private int lastDetectedTagId = -1;

    // --- 4 Distinct Arrays for your Tag Clusters ---
    //pipeline 0
    public static final int[] RED_OPPOSITE_IDS = {30, 31, 32, 33}; // Pipeline 0
   //Red up initial- pipeline 1
    public static final int[] RED_AUDIENCE_IDS  = {34, 35, 36, 37}; // Pipeline 1

    //pipeline 2
    public static final int[] BLUE_AUDIENCE_IDS = {38, 39, 40, 41}; // Pipeline 2
   //Blue Up initial - pipeline 3
    public static final int[] BLUE_OPPOSITE_IDS = {42, 43, 44, 45}; // Pipeline 3

    //pipeline # 4 - RED consolidated
    public static final int[] RED_IDS = {30, 31, 32, 33,34, 35, 36, 37};

    //pipeline # 5 - Blue consolidated
    public static final int[] BLUE_IDS = {38, 39, 40, 41, 42, 43, 44, 45};

    public static class TagReading {
        public final boolean valid;
        public final int tagId;
        public final double distanceInches;

        public TagReading(boolean valid, int tagId, double distanceInches) {
            this.valid = valid;
            this.tagId = tagId;
            this.distanceInches = distanceInches;
        }
    }

    public void init(HardwareMap hardwareMap, int pipelineIndex, int[] targetClusterIds, Telemetry telemetry) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        this.activeClusterIds = targetClusterIds;

        limelight.pipelineSwitch(pipelineIndex);
        this.activePipelineIndex = pipelineIndex;
        limelight.start();
    }

    // Add this method so TeleOp / Calibration can switch pipelines and tag IDs live
    public void setCluster(int pipelineIndex, int[] targetClusterIds) {
        this.activePipelineIndex = pipelineIndex;
        this.activeClusterIds = targetClusterIds;
        if (limelight != null) {
            limelight.pipelineSwitch(pipelineIndex);
        }
    }

    public int getActivePipelineIndex() {
        return activePipelineIndex;
    }

    public int getVisibleClusterTagCount() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return 0;
        }

        List<LLResultTypes.FiducialResult> visibleTags = result.getFiducialResults();
        int matchCount = 0;

        // Traditional for loop using an index 'i'
        for (int i = 0; i < visibleTags.size(); i++) {
            LLResultTypes.FiducialResult tag = visibleTags.get(i);
            int detectedId = tag.getFiducialId();

            // Traditional for loop to check against our cluster array
            for (int j = 0; j < activeClusterIds.length; j++) {
                if (detectedId == activeClusterIds[j]) {
                    matchCount++;
                    break; // Stop checking this tag once a match is found
                }
            }
        }
        return matchCount;
    }

    public int getTotalVisibleTagCount() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return 0;
        }
        return result.getFiducialResults().size();
    }

    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }


    public String getVisibleTagIdsString() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return "No valid result";
        }

        List<LLResultTypes.FiducialResult> visibleTags = result.getFiducialResults();
        if (visibleTags.isEmpty()) {
            return "None in view";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < visibleTags.size(); i++) {
            sb.append(visibleTags.get(i).getFiducialId());
            if (i < visibleTags.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public double getLastValidDistanceInches() {
        return lastValidDistanceInches;
    }

    public int getLastDetectedTagId() {
        return lastDetectedTagId;
    }

    private boolean matchesActiveCluster(int detectedId) {
        if (activeClusterIds == null) {
            return true;
        }
        for (int j = 0; j < activeClusterIds.length; j++) {
            if (detectedId == activeClusterIds[j]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the nearest visible tag in activeClusterIds (or any visible tag if
     * activeClusterIds is null) along with its 3D distance in inches.
     */
    public TagReading getNearestClusterTag() {
        if (limelight == null) {
            return new TagReading(false, -1, -1.0);
        }

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return new TagReading(false, -1, -1.0);
        }

        List<LLResultTypes.FiducialResult> visibleTags = result.getFiducialResults();
        double bestDistanceInches = Double.MAX_VALUE;
        int bestTagId = -1;

        for (int i = 0; i < visibleTags.size(); i++) {
            LLResultTypes.FiducialResult tag = visibleTags.get(i);
            int detectedId = tag.getFiducialId();

            if (!matchesActiveCluster(detectedId)) {
                continue;
            }

            Pose3D camToTarget = tag.getTargetPoseCameraSpace();
            if (camToTarget != null && camToTarget.getPosition() != null) {
                double x = camToTarget.getPosition().x;
                double y = camToTarget.getPosition().y;
                double z = camToTarget.getPosition().z;
                double distInches = Math.sqrt(x * x + y * y + z * z) * METERS_TO_INCHES;

                if (distInches > 0.0 && distInches < bestDistanceInches) {
                    bestDistanceInches = distInches;
                    bestTagId = detectedId;
                }
            }
        }

        if (bestTagId != -1) {
            lastDetectedTagId = bestTagId;
            lastValidDistanceInches = bestDistanceInches;
            return new TagReading(true, bestTagId, bestDistanceInches);
        }

        return new TagReading(false, -1, -1.0);
    }
}

