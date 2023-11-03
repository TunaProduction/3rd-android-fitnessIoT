package com.etime.training_presentation.util

import com.polar.sdk.api.model.PolarAccelerometerData

fun detectFall(polarData: PolarAccelerometerData): Boolean {
    val heavyFallThreshold = 12000  // Increase this threshold for detecting only heavy falls in millig
    val durationThreshold = 500_000_000  // Duration threshold in nanoseconds
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
}