package com.limetac.scanner.utils

import android.content.Context
import android.content.Intent
import com.limetac.scanner.R
import com.limetac.scanner.utils.ToastUtil.createShortToast

object IntentUtil {

    fun openMainModule(context: Context, clazz: Class<*>) {
        if (BluetoothUtil.isBluetoothEnabled) {
            context.startActivity(Intent(context, clazz))
        } else {
            createShortToast(context, context.getString(R.string.enableBluetoothMessage))
        }
    }
}