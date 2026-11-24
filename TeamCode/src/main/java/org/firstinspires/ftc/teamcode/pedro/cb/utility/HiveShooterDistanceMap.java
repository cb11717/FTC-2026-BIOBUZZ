package org.firstinspires.ftc.teamcode.pedro.cb.utility;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Map;
import java.util.TreeMap;

public class HiveShooterDistanceMap {

    public static class ShooterVelocities {
        public final double pollenTps;
        public final double nectarTps;

        public ShooterVelocities(double pollenTps, double nectarTps) {
            this.pollenTps = pollenTps;
            this.nectarTps = nectarTps;
        }
    }

    // Map of Distance (inches) -> Target (Pollen TPS, Nectar TPS)
    private final TreeMap<Double, ShooterVelocities> distanceMap = new TreeMap<>();

    // Live trim offsets (TPS) applied on top of mapped velocities
    private double pollenTrimTps = 0.0;
    private double nectarTrimTps = 0.0;

    public HiveShooterDistanceMap() {
        // Initial calibration points: (distanceInches, pollenTps, nectarTps)
        // Update these with your measured Hive shot values during practice
        addPoint(18.0, 1490.0, 2075.0);
        addPoint(36.0, 1800.0, 2200.0);
        addPoint(54.0, 2300.0, 2300.0);
    }

    public void addPoint(double distanceInches, double pollenTps, double nectarTps) {
        if (distanceInches > 0.0) {
            distanceMap.put(distanceInches, new ShooterVelocities(pollenTps, nectarTps));
        }
    }

    public void removePoint(double distanceInches) {
        distanceMap.remove(distanceInches);
    }

    public void clearPoints() {
        distanceMap.clear();
    }

    public int size() {
        return distanceMap.size();
    }

    // --- Trim Controls (pass positive to increase, negative to decrease) ---

    public void adjustPollenTrim(double deltaTps) {
        pollenTrimTps += deltaTps;
    }

    public void adjustNectarTrim(double deltaTps) {
        nectarTrimTps += deltaTps;
    }

    public void increasePollenTrim(double stepTps) {
        pollenTrimTps += Math.abs(stepTps);
    }

    public void decreasePollenTrim(double stepTps) {
        pollenTrimTps -= Math.abs(stepTps);
    }

    public void increaseNectarTrim(double stepTps) {
        nectarTrimTps += Math.abs(stepTps);
    }

    public void decreaseNectarTrim(double stepTps) {
        nectarTrimTps -= Math.abs(stepTps);
    }

    public void resetTrims() {
        pollenTrimTps = 0.0;
        nectarTrimTps = 0.0;
    }

    public double getPollenTrimTps() {
        return pollenTrimTps;
    }

    public double getNectarTrimTps() {
        return nectarTrimTps;
    }

    // --- Practice Calibration Helpers ---

    /**
     * Records a (distanceInches -> pollenTps, nectarTps) entry rounded to the nearest 0.5 in.
     */
    public void recordCalibrationPoint(double distanceInches, double pollenTps, double nectarTps) {
        if (distanceInches > 0.0) {
            double keyInches = Math.round(distanceInches * 2.0) / 2.0;
            addPoint(keyInches, pollenTps, nectarTps);
        }
    }

    /**
     * Returns a single-line summary of all mapped distances and velocities sorted by distance.
     */
    public String getMapSummaryString() {
        if (distanceMap.isEmpty()) {
            return "Empty map";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Double, ShooterVelocities> entry : distanceMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(entry.getKey())
                    .append("in: P=")
                    .append(Math.round(entry.getValue().pollenTps))
                    .append(", N=")
                    .append(Math.round(entry.getValue().nectarTps));
        }
        return sb.toString();
    }

    // --- Distance Interpolation ---

    /**
     * Interpolates Pollen and Nectar TPS for a given distance in inches,
     * then applies live trims and clamps to [0, maxTps].
     */
    public ShooterVelocities getVelocitiesForDistance(double distanceInches, double maxTps) {
        if (distanceMap.isEmpty()) {
            return new ShooterVelocities(
                    clamp(pollenTrimTps, 0.0, maxTps),
                    clamp(nectarTrimTps, 0.0, maxTps)
            );
        }

        Map.Entry<Double, ShooterVelocities> floor = distanceMap.floorEntry(distanceInches);
        Map.Entry<Double, ShooterVelocities> ceiling = distanceMap.ceilingEntry(distanceInches);

        ShooterVelocities base;
        if (floor == null) {
            base = ceiling.getValue();
        } else if (ceiling == null || Math.abs(ceiling.getKey() - floor.getKey()) < 1e-6) {
            base = floor.getValue();
        } else {
            double t = (distanceInches - floor.getKey()) / (ceiling.getKey() - floor.getKey());
            double p = floor.getValue().pollenTps + t * (ceiling.getValue().pollenTps - floor.getValue().pollenTps);
            double n = floor.getValue().nectarTps + t * (ceiling.getValue().nectarTps - floor.getValue().nectarTps);
            base = new ShooterVelocities(p, n);
        }

        return new ShooterVelocities(
                clamp(base.pollenTps + pollenTrimTps, 0.0, maxTps),
                clamp(base.nectarTps + nectarTrimTps, 0.0, maxTps)
        );
    }

    public void addTelemetry(Telemetry telemetry, double currentPollenActual, double currentNectarActual) {
        telemetry.addLine("----------HIVE VELOCITY MAP--------");
        telemetry.addData("Map Points", distanceMap.size());
        telemetry.addData("Pollen Trim TPS", pollenTrimTps);
        telemetry.addData("Nectar Trim TPS", nectarTrimTps);
        telemetry.addData("Pollen Actual TPS", Math.round(currentPollenActual * 10.0) / 10.0);
        telemetry.addData("Nectar Actual TPS", Math.round(currentNectarActual * 10.0) / 10.0);
        telemetry.addData("Map Entries", getMapSummaryString());
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}