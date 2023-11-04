package com.etime.training_presentation.profile

import TTTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.etime.core_ui.R
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTProgressBar
import com.etime.training_presentation.data.Profile

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNextClick: () -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var weight by remember {
        mutableStateOf("")
    }

    var height by remember {
        mutableStateOf("")
    }

    var age by remember {
        mutableStateOf("")
    }

    var userType by remember {
        mutableStateOf("")
    }

    val loading = viewModel.loading.collectAsState()
    val finished = viewModel.finished.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TTTextField(
            value = name,
            onValueChange = {
                if(it.all { char ->
                        char.isLetter() || char.isWhitespace()
                    }) name = it
            },
            placeHolder = "Name"
        )

        Spacer(modifier = Modifier.height(5.dp))

        TTTextField(
            value = age,
            onValueChange = {
                if(it.all { char ->
                        char.isDigit() || char.isWhitespace()
                    }) age = it
            },
            placeHolder = "Age"
        )

        Spacer(modifier = Modifier.height(5.dp))

        TTTextField(
            value = height,
            onValueChange = {
                if(it.all { char ->
                        char.isDigit() || char.isWhitespace()
                    }) height = it
            },
            placeHolder = "Height"
        )

        Spacer(modifier = Modifier.height(5.dp))

        TTTextField(
            value = weight,
            onValueChange = {
                if(it.all { char ->
                        char.isDigit() || char.isWhitespace()
                    }) weight = it
            },
            placeHolder = "Weight"
        )

        Spacer(modifier = Modifier.height(5.dp))

        TTTextField(
            value = userType,
            onValueChange = {
                if(it.all { char ->
                        char.isLetter() || char.isWhitespace()
                    }) userType = it
            },
            placeHolder = "Type"
        )

        Spacer(modifier = Modifier.height(5.dp))

        TTButton(
            text = "Save"
        ){
            viewModel.createOrEditUser(
                Profile(
                    userType = userType,
                    name = name,
                    weight = weight,
                    height = height,
                    age = age
                )
            )
        }
    }

    if(loading.value) {
        TTProgressBar()
    }

    if(finished.value) {
        LaunchedEffect(true) {
            onNextClick()
        }
    }
}