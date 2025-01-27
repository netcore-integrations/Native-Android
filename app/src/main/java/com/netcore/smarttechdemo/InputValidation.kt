package com.netcore.smarttechdemo

import android.content.Context
import android.widget.EditText

class InputValidation(private val context: Context) {

    fun isTextFilled(textEditText: EditText, message: String): Boolean {
        val value = textEditText.text.toString().trim()
        if (value.isEmpty()) {
            textEditText.error = message
            return false
        } else
            return true
    }

    fun isPassMatch(textEditText1: EditText, textEditText2: EditText, message: String): Boolean {
        val value1 = textEditText1.text.toString().trim()
        val value2 = textEditText2.text.toString().trim()
        if (!value1.contentEquals(value2)) {
            textEditText1.error = message
            return false
        } else {
            return true
        }

    }
}