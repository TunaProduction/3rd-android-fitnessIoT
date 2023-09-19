package com.etime.training_presentation.util

import android.util.Log
import com.polar.sdk.api.model.PolarAccelerometerData
import java.lang.Math.abs
import java.lang.Math.sqrt
import kotlin.math.pow

var stepCount = 0
var strideLength = 0.7  // Calibrate this value based on your stride
var distance = 0.0  // Estimated distance walked, in meters

// Kalman filter variables
var kalmanGain = 0.0
var estimate = 0.0
var estimateError = 1.0
var measurementError = 0.1
var processNoise = 0.0001

var sensitivityThreshold = 30.0  // Reduced threshold for greater sensitivity

fun getWalkedDistance(samples: List<PolarAccelerometerData.PolarAccelerometerDataSample>): Pair<Double, Int> {
    for (sample in samples) {
        val x = sample.x
        val y = sample.y
        val z = sample.z

        // Calculate resultant vector and remove gravity (1g = 1000 millig)
        val aNet = sqrt((x * x + y * y + z * z).toDouble()) - 1000

        // Apply Kalman filter
        estimateError += processNoise
        kalmanGain = estimateError / (estimateError + measurementError)
        val prevEstimate = estimate
        estimate = estimate + kalmanGain * (aNet - estimate)
        estimateError = (1 - kalmanGain) * estimateError

        Log.d("DEBUG", "aNet: $aNet, estimate: $estimate, prevEstimate: $prevEstimate, kalmanGain: $kalmanGain")

        // Step detection: Check if the absolute difference crosses the sensitivity threshold
        if (abs(prevEstimate - estimate) > sensitivityThreshold) {
            stepCount++
            Log.d("DEBUG", "Step detected. Total steps: $stepCount")
        }
    }

// Calculate estimated distance walked
    distance = stepCount * strideLength
    Log.d("DEBUG", "Estimated distance walked: $distance meters")

    return Pair(distance, stepCount)
}