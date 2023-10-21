package com.etime.core_ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.etime.core_ui.LocalSpacing

@Composable
fun TTTrainingRow(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    valueTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    icon : Int? = null
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
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
                style = textStyle.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(LocalSpacing.current.spaceSmall)
            )
        }
        Text(
            text = value,
            style = valueTextStyle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(LocalSpacing.current.spaceSmall)
        )
    }
}