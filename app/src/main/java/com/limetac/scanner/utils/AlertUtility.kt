package com.limetac.scanner.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import com.limetac.scanner.R


@JvmOverloads
fun showAlert(
    context: Context,
    title: String?,
    message: String,
    @DrawableRes icon: Int?,
    positiveBtnMsg: String,
    positiveBtnAction: () -> Unit = {},
    negativeBtnMsg: String?,
    negativeBtnAction: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton(
            positiveBtnMsg
        ) { dialog, _ ->
            positiveBtnAction.invoke()
            dialog.dismiss()
        }
    icon?.let {
        builder.setIcon(icon)
    }
    title?.let {
        builder.setTitle(it)
    }
    negativeBtnMsg?.let {
        builder.setNegativeButton(negativeBtnMsg) { dialog, _ ->
            negativeBtnAction.invoke()
            dialog.dismiss()

        }
        builder.create().show()
    }
}

fun showError(context: Context, errorMessage: String) {
    AlertDialog.Builder(context)
        .setTitle(R.string.alert)
        .setCancelable(false)
        .setMessage(errorMessage)
        .setPositiveButton(
            R.string.ok
        ) { dialog, _ -> dialog.dismiss() }
        .create().show()
}

fun showOKDialog(context: Context, message: String, title: String?) {
    val builder = AlertDialog.Builder(context)
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton(
            R.string.ok
        ) { dialog, _ -> dialog.dismiss() }
        .create()
    title?.let {
        builder.setTitle(it)
    }
    builder.show()
}








