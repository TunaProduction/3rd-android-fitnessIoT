package com.etime.core_ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.etime.core_ui.BatteryYellow
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.Orange
import com.etime.core_ui.R

@Composable
fun TTBattery(
    level: Int,
    size: Dp = LocalSpacing.current.spaceLarge
) {
    var iconId: Int
    var color: Color

    when(level) {
        0 -> {
            iconId = R.drawable.ic_battery_0
            color = Color.Black
        }

         in 1..10 -> {
             iconId = R.drawable.ic_battery_1
             color = Color.Red
         }

         in 11..20 -> {
             iconId = R.drawable.ic_battery_2
             color = Orange
         }

         in 21..49 -> {
             iconId = R.drawable.ic_battery_3
             color = Orange
         }

         in 50..60 -> {
             iconId = R.drawable.ic_battery_4
             color = BatteryYellow
         }

         in 61..79 -> {
             iconId = R.drawable.ic_battery_5
             color = BatteryYellow
         }

         in 80..99 -> {
             iconId = R.drawable.ic_battery_6
             color = Color.Green
         }

         100 -> {
             iconId = R.drawable.ic_battery_full
             color = Color.Green
         }

        else -> {
            iconId = R.drawable.ic_battery_unknown
            color = Color.Black
        }
    }

    Icon(
        modifier = Modifier.size(size),
        painter = painterResource(id = iconId),
        contentDescription = "Battery level",
        tint = color
    )
}