package com.kdan.authorization.screen

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kdan.authorization.R
import com.kdan.authorization.utility.Utility
import com.kdan.authorization.viewmodel.AuthViewModel

@Composable
fun ShowAlertDialog(
    viewModel: AuthViewModel,
) {
    val context = LocalContext.current
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.dialog_title))
        },
        text = {
            Text(Utility.getMessages(context, viewModel.messageCodes)
                .joinToString("\n\n"))
        },
        onDismissRequest = { },
        buttons = {
            Button(onClick = {
                Utility.turnOffDialog(viewModel.showDialog)
                Utility.clearMessageCodes(viewModel.messageCodes)
            }) {
                Text(stringResource(id = R.string.dialog_text_button_okay))
            }
        }

    )
}