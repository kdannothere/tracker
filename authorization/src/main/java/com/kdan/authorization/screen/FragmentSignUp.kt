package com.kdan.authorization.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.authorization.R.string
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.utility.Utility
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FragmentSignUp(
    navController: NavHostController,
    context: Context,
    viewModel: AuthViewModel = viewModel(),
) {
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically)
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            item {
                Text(text = "Registration")
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = emailState.value,
                    onValueChange = {
                        run {
                            emailState.value = it
                            viewModel.email = emailState.value.text
                        }
                    },
                    singleLine = true,
                    placeholder = { Text(stringResource(string.hint_email)) },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboard?.hide()
                        }
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = password.value,
                    onValueChange = {
                        run {
                            password.value = it
                            viewModel.password = password.value.text
                        }
                    },
                    singleLine = true,
                    placeholder = { Text(stringResource(string.hint_password)) },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboard?.hide()
                        }
                    )
                )
                if (viewModel.showDialog.value) {
                    ShowAlertDialog(context = context)
                }
                Spacer(modifier = Modifier.height(40.dp))
                TextButton(
                    onClick = {
                        scope.launch {
                            signUp(
                                auth = viewModel.auth,
                                email = viewModel.email,
                                password = viewModel.password,
                                showDialog = viewModel.showDialog,
                                messageCodes = viewModel.messageCodes
                            )
                            keyboard?.hide()
                        }
                    }) {
                    Text(text = stringResource(id = string.sign_up))
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextButton(
                    onClick = {
                        navController.navigate(RoutesAuth.fragmentSignIn)
                    }) {
                    Text(text = stringResource(id = string.back_to_sign_in))
                }
            }
        }
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    showDialog: MutableState<Boolean>,
    messageCodes: MutableList<Int>,
) {
    val isEmailOkay = Utility.checkEmail(email, messageCodes)
    val isPasswordOkay = Utility.checkPassword(password, messageCodes)
    if (!isEmailOkay || !isPasswordOkay) {
        Utility.turnOnDialog(showDialog)
        return
    }
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Utility.addMessageCode(string.message_success, messageCodes)
            } else {
                Utility.addMessageCode(string.message_failure, messageCodes)
            }
            Utility.turnOnDialog(showDialog)
        }
}