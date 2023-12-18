package com.etime.training_presentation.util

import com.polar.sdk.api.model.PolarAccelerometerData
import kotlin.math.abs
import kotlin.math.max

fun detectFall(polarData: PolarAccelerometerData, userHeight: Int): Boolean {
    val baseFallThreshold = 6000  // Base threshold for detecting falls
    val heightFactor = calculateHeightFactor(userHeight)  // Adjust threshold based on user's height
    val heavyFallThreshold = baseFallThreshold + heightFactor
    val durationThreshold = 400_000_000  // Duration threshold in nanoseconds
    val smoothingWindow = 5  // Number of samples for moving average
    var heavyFallDetected = false
    var startFallTime = 0L
    var smoothedMagnitude = 0.0

    for (i in polarData.samples.indices) {
        val sample = polarData.samples[i]
        val magnitude = calculateWeightedMagnitude(sample)
        //val magnitude = Math.sqrt((sample.x * sample.x + sample.y * sample.y + sample.z * sample.z).toDouble())

        // Moving average for smoothing
        smoothedMagnitude = (smoothedMagnitude * (smoothingWindow - 1) + magnitude) / smoothingWindow

        if (smoothedMagnitude > heavyFallThreshold && startFallTime == 0L) {
            startFallTime = sample.timeStamp
        }

        if (startFallTime > 0L) {
            if (sample.timeStamp - startFallTime < durationThreshold) {
                if (isJogging(polarData.samples.subList(max(0, i - smoothingWindow), i + 1))) {
                    startFallTime = 0L  // Reset if jogging is detected
                } else {
                    heavyFallDetected = true
                    break
                }
                /*heavyFallDetected = true
                break*/
            } else {
                startFallTime = 0L
            }
        }
    }

    return heavyFallDetected
}

fun calculateHeightFactor(height: Int): Int {
    val averageHeight = 170  // average height in cm
    val heightAdjustmentFactor = 50  // acceleration threshold adjustment per cm

    // Implement logic to adjust threshold based on height
    return (height - averageHeight) * heightAdjustmentFactor
}

fun calculateWeightedMagnitude(sample: PolarAccelerometerData.PolarAccelerometerDataSample): Double {
    val weightFactorY = 2.0  // Adjust this factor to give more importance to the Y-axis
    val weightedY = sample.y * weightFactorY
    return Math.sqrt((sample.x * sample.x + weightedY * weightedY + sample.z * sample.z).toDouble())
}

fun isJogging(samples: List<PolarAccelerometerData.PolarAccelerometerDataSample>): Boolean {
    if (samples.isEmpty()) return false

    val peakThreshold = 1200 // Adjust this based on your data
    val minPeakInterval = 200_000_000 // 0.2 seconds in nanoseconds
    val maxPeakInterval = 600_000_000 // 0.6 seconds in nanoseconds

    var lastPeakTime = 0L
    var peaks = 0

    for (sample in samples) {
        val magnitude = Math.sqrt((sample.x * sample.x + sample.y * sample.y + sample.z * sample.z).toDouble())

        if (magnitude > peakThreshold) {
            if (lastPeakTime == 0L || (sample.timeStamp - lastPeakTime) in minPeakInterval..maxPeakInterval) {
                peaks++
                lastPeakTime = sample.timeStamp
            }
        }
    }

    return peaks >= 3 // Confirm jogging if there are at least 3 peaks
}
/*fun detectFall(polarData: PolarAccelerometerData): Boolean {
    val heavyFallThreshold = 6000  // Increase this threshold for detecting only heavy falls in millig
    val durationThreshold = 400_000_000  // Duration threshold in nanoseconds
    var heavyFallDetected = false
    var startFallTime = 0L
    for (sample in polarData.samples) {
        val magnitude = Math.sqrt((sample.x * sample.x + sample.y * sample.y + sample.z * sample.z).toDouble()).toInt()

        if (magnitude > heavyFallThreshold && startFallTime == 0L) {
            // Potential heavy fall detected
            startFallTime = sample.timeStamp
        }

        if (startFallTime > 0L) {
            // Check if potential fall duration meets the criteria
            if (sample.timeStamp - startFallTime < durationThreshold) {
                    heavyFallDetected = true
                break
            } else {
                // Reset start time as this is a false alarm
                startFallTime = 0L
            }
        }
    }

    return heavyFallDetected
}*/