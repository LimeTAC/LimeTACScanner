package com.limetac.scanner.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {
    fun createLongToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @JvmStatic
    fun createShortToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}