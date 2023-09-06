package com.etime.auth_presentation.login

import TTInputText
import com.etime.core_ui.R
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun LoginScreen() {
    var email by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.auth_login_title)
        )

        TTInputText(text = email, label = "my email", onTextChanged = {
            if(it.all { char ->
                    char.isLetter() || char.isWhitespace()
                }) email = it
        })
    }
}