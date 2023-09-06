package com.etime.auth_presentation.login

import TTTextField
import android.util.Log
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
import androidx.compose.ui.unit.dp
import com.etime.core_ui.components.TTButton

@Composable
fun LoginScreen() {
    var email by remember {
        mutableStateOf("")
    }

    var email2 by remember {
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

        TTTextField(value = email, onValueChange = {
            if(it.all { char ->
                    char.isLetter() || char.isWhitespace()
                }) email = it
        },
            icon = R.drawable.ic_email
        )

        Spacer(modifier = Modifier.height(5.dp))
        TTTextField(value = email2, onValueChange = {
            if(it.all { char ->
                    char.isLetter() || char.isWhitespace()
                }) email2 = it
        },
            icon = R.drawable.ic_password
        )

        Spacer(modifier = Modifier.height(5.dp))

        TTButton(
            text = "Sign in"
        ){
            Log.d("holi", "crayoli")
        }
    }
}