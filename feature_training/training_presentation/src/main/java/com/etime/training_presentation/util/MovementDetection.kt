package com.etime.training_presentation.util

import com.polar.sdk.api.model.PolarAccelerometerData
import java.lang.Math.sqrt
import kotlin.math.pow


    private val heavyMovementThreshold = 1200.0
private val heavyMovementDuration = 2 // Example value, adjust as needed based on your testing
private var consecutiveHeavyMovements = 0

    fun isHeavyMovement(polarAccelerometerData: PolarAccelerometerData): Boolean {
        polarAccelerometerData.samples.forEach {
            val magnitude = sqrt(it.x.toDouble().pow(2) + it.y.toDouble().pow(2) + it.z.toDouble().pow(2))
            if (magnitude > heavyMovementThreshold) {
                consecutiveHeavyMovements++
                if (consecutiveHeavyMovements >= heavyMovementDuration) {
                    return true
                }
            } else {
                consecutiveHeavyMovements = 0 // reset the count if a sample is below the threshold
            }
        }
        return false
    }
