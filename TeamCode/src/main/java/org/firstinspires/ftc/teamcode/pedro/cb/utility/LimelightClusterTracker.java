package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.pedro.cb.utility.TelemetryUtil;

import java.util.List;

public class LimelightClusterTracker {

    private Limelight3A limelight;
    private int[] activeClusterIds;

    // --- 4 Distinct Arrays for your Tag Clusters ---
    //pipeline 0
    public static final int[] RED_OPPOSITE_IDS = {30, 31, 32, 33}; // Pipeline 0
   //Red up initial- pipeline 1
    public static final int[] RED_AUDIENCE_IDS  = {34, 35, 36, 37}; // Pipeline 1

    //pipeline 2
    public static final int[] BLUE_AUDIENCE_IDS = {38, 39, 40, 41}; // Pipeline 2
   //Blue Up initial - pipeline 3
    public static final int[] BLUE_OPPOSITE_IDS = {42, 43, 44, 45}; // Pipeline 3

    public void init(HardwareMap hardwareMap, int pipelineIndex, int[] targetClusterIds, Telemetry telemetry) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        this.activeClusterIds = targetClusterIds;

        limelight.pipelineSwitch(pipelineIndex);
        limelight.start();
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

    // Add this method to LimelightClusterTracker.java
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
}

