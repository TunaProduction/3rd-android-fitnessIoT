package com.etime.core_ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import com.etime.core_ui.LocalSpacing

@Composable
fun TTTrainingCell(
    name: String,
    value: String,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    icon : Int? = null
) {
    Column {
        Row {
            icon?.let{
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Favorite icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = name,
                style = textStyle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(LocalSpacing.current.spaceSmall)
            )
        }
        Text(
            text = value,
            style = textStyle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(LocalSpacing.current.spaceSmall)
        )
    }
}