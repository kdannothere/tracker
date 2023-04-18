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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.kdan.authorization.R.string
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.utility.Utility
import com.kdan.authorization.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FragmentSignIn(
    navController: NavHostController,
    context: Context,
    routeToTracker: String,
    viewModelStoreOwner: ViewModelStoreOwner,
    viewModel: AuthViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
) {
    val email = remember { mutableStateOf(TextFieldValue("")) }
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
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            item {
                TextField(
                    value = email.value,
                    onValueChange = {
                        run {
                            email.value = it
                            viewModel.email = email.value.text
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
                Spacer(modifier = Modifier.height(20.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { navController.navigate(RoutesAuth.fragmentRestorePassword) }) {
                        Text(text = stringResource(id = string.forgot_password))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextButton(
                    onClick = {
                        scope.launch {
                            signIn(
                                auth = viewModel.auth,
                                navHostController = navController,
                                email = viewModel.email,
                                password = viewModel.password,
                                showDialog = viewModel.showDialog,
                                messageCodes = viewModel.messageCodes,
                                routeToTracker = routeToTracker
                            )
                            keyboard?.hide()
                        }
                    }) {
                    Text(text = stringResource(id = string.sign_in))
                }
                if (viewModel.showDialog.value) {
                    ShowAlertDialog(context, viewModelStoreOwner)
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextButton(onClick = {
                    navController.navigate(RoutesAuth.fragmentSignUp)
                }) {
                    Column {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = stringResource(id = string.have_no_account))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = stringResource(id = string.sign_up))
                    }
                }
            }
        }
    }
}

private fun signIn(
    auth: FirebaseAuth,
    navHostController: NavHostController,
    email: String,
    password: String,
    showDialog: MutableState<Boolean>,
    messageCodes: MutableList<Int>,
    routeToTracker: String,
) {
    val isEmailOkay = Utility.checkEmail(email, messageCodes)
    val isPasswordOkay = Utility.checkPassword(password, messageCodes)
    if (!isEmailOkay || !isPasswordOkay) {
        Utility.turnOnDialog(showDialog)
        return
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navHostController.navigate(routeToTracker)
            } else {
                Utility.addMessageCode(string.message_failure, messageCodes)
                Utility.turnOnDialog(showDialog)
            }
        }
}