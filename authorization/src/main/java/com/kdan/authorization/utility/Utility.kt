package com.kdan.authorization.utility

import android.content.Context
import androidx.compose.runtime.MutableState
import com.kdan.authorization.R
import java.util.regex.Pattern

object Utility {

    fun checkEmail(
        email: String,
        messageCodes: MutableList<Int>,
    ): Boolean {
        if (email.isBlank()) {
            addMessageCode(R.string.message_email_blank, messageCodes)
            return false
        }
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        val result = emailPattern.matcher(email).matches()
        if (!result) {
            addMessageCode(R.string.message_rule_email, messageCodes)
        }
        return result
    }

    fun checkPassword(
        password: String,
        messageCodes: MutableList<Int>,
    ): Boolean {
        if (password.isBlank()) {
            addMessageCode(R.string.message_password_blank, messageCodes)
            return false
        }
        val passwordPattern = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&*!?+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,20}" +               //from 8 to 20 characters
                "$")
        val result = passwordPattern.matcher(password).matches()
        if (!result) {
            addMessageCode(R.string.message_rule_password, messageCodes)
        }
        return result
    }

    fun turnOnDialog(showDialog: MutableState<Boolean>) = run { showDialog.value = true }
    fun turnOffDialog(showDialog: MutableState<Boolean>) = run { showDialog.value = false }
    fun addMessageCode(messageCode: Int, messageCodes: MutableList<Int>) =
        run { messageCodes += messageCode }

    fun clearMessageCodes(messageCodes: MutableList<Int>) = messageCodes.clear()
    fun getMessages(context: Context, messageCodes: MutableList<Int>): List<String> {
        val messages = mutableListOf<String>()
        messageCodes.forEach {
            messages += "- ${context.getString(it)}"
        }
        return messages
    }

}