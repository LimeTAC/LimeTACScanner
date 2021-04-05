package com.limetac.scanner.utils

import android.bluetooth.BluetoothAdapter

object BluetoothUtil {
    val isBluetoothEnabled: Boolean
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return mBluetoothAdapter?.isEnabled ?: false
        }
}