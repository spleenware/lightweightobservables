package com.spleenware.lwosample

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by powellsc on 23/1/19.
 */
class NumberInputDialog(private val context: Context, val max: Int, val min: Int = 1) {

    fun show(title: String, hint: String, onSubmit: (value: Int) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.number_input_dialog, null)

        val dlg = AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setCancelable(true)
                .create()

        val input = view.findViewById<EditText>(R.id.input)
        input.hint = hint

        view.findViewById<View>(R.id.cancel_button).setOnClickListener {
            dlg.dismiss()
        }
        view.findViewById<View>(R.id.enter_button).setOnClickListener {
            val value = try {
                input.getText().toString().toInt()
            } catch (ex: NumberFormatException) {
                0
            }

            if (value < min) {
                input.setError("Must be at least $min")
            } else if (value > max) {
                input.setError("Must be not be over $max")
            } else {
                dlg.dismiss()
                onSubmit(value)
            }
        }

        dlg.show()

        input.postDelayed( {
            input.requestFocus()
            (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(input, 0)
        }, 200L)
    }
}