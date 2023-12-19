package com.eTime.a3rd_android_fitnesslot.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eTime.a3rd_android_fitnesslot.ui.theme._3rdandroidfitnessloTTheme
import com.etime.core_ui.R
import com.etime.core_ui.components.TTBattery
import com.etime.core_ui.components.TTCircleIcon
import com.etime.core_ui.components.TTProgressBar
import com.etime.core_ui.components.TTTrainingCell

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _3rdandroidfitnessloTTheme {
      /*  TTTrainingCell(
            name = "Distance",
            value = "9.0 m",
        )*/

        TTBattery(level = 65)
    }
}