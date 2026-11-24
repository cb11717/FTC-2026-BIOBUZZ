package org.firstinspires.ftc.teamcode.pedro.cb.utility;

public class VelocityPIDF {
    private final double kP;
    private final double kI;
    private final double kD;
    private final double kF;
    private final double integralMax;

    private double targetVelocity = 0.0;
    private double integralSum = 0.0;
    private double lastError = 0.0;
    private long lastNanoTime = 0L;
    private boolean hasLastState = false;

    public VelocityPIDF(double kP, double kI, double kD, double kF, double integralMax) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        this.integralMax = integralMax;
    }

    public void setTargetVelocity(double targetTPS) {
        if (Math.abs(targetTPS - this.targetVelocity) > 1e-6) {
            this.targetVelocity = targetTPS;
            // Reset integral and derivative history when target speed changes
            integralSum = 0.0;
            lastError = 0.0;
            hasLastState = false;
        }
    }

    public void reset() {
        integralSum = 0.0;
        lastError = 0.0;
        lastNanoTime = 0L;
        hasLastState = false;
    }

    public double update(double currentVelocity) {
        long now = System.nanoTime();

        if (!hasLastState) {
            lastNanoTime = now;
            lastError = targetVelocity - currentVelocity;
            hasLastState = true;
            double ff = kF * targetVelocity;
            return clamp(ff, -1.0, 1.0);
        }

        double dt = (now - lastNanoTime) / 1_000_000_000.0;
        lastNanoTime = now;

        // Guard against tiny or stalled loop deltas
        if (dt <= 1e-6) {
            dt = 1e-6;
        }

        double error = targetVelocity - currentVelocity;

        // Accumulate and clamp integral to prevent windup during ball impact
        integralSum += error * dt;
        integralSum = clamp(integralSum, -integralMax, integralMax);

        double derivative = (error - lastError) / dt;
        lastError = error;

        // Feed-forward holds steady state; P, I, D handle load dips & recovery
        double feedForward = kF * targetVelocity;
        double output = feedForward + (kP * error) + (kI * integralSum) + (kD * derivative);

        return clamp(output, -1.0, 1.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}