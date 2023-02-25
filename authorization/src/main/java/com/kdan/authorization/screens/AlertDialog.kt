package com.kdan.authorization.screens

import android.content.Context
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kdan.authorization.AuthViewModel
import com.kdan.authorization.utility.Utility

@Composable
fun Alert(
    viewModel: AuthViewModel = viewModel(),
    context: Context,
) {
    AlertDialog(
        title = {
            Text(text = "Messages")
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
                Text("Okay")
            }
        }

    )
}