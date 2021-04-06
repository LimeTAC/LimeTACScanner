package com.limetac.scanner.utils

import android.content.Context
import android.content.SharedPreferences

class Preference(pContext: Context?) {
    private val prefName = "limetac_prefs"
    private var context: Context? = null
    private var pref: SharedPreferences? = null

    /**
     *
     * @param pRef
     * @param value
     */
    fun saveStringInPrefrence(pRef: String?, value: String?) {
        try {
            val editor = pref?.edit()
            editor?.putString(pRef, value)
            editor?.apply()
        } catch (e: Exception) {
        }
    }

    fun saveBooleanFlagInPrefrence(key: String?, value: Boolean) {
        try {
            val editor = pref?.edit()
            editor?.putBoolean(key, value)
            editor?.apply()
        } catch (e: Exception) {
        }
    }

    fun getBooleanFlagPrefrence(key: String?, defaultValue: Boolean): Boolean {
        return if(pref!=null) pref!!.getBoolean(key, defaultValue) else false
    }

    /**
     *
     * @param pRef
     * @param defaultValue
     * @return String
     */
    fun getStringPrefrence(
        pRef: String?,
        defaultValue: String?
    ): String? {
        return pref?.getString(pRef, defaultValue)
    }

    /**
     *
     * @param pRef
     * @param value
     */
    fun saveIntegerInPrefrence(pRef: String?, value: Int) {
        try {
            val editor = pref?.edit()
            editor?.putInt(pRef, value)
            editor?.apply()
        } catch (e: Exception) {
        }
    }

    /**
     *
     * @param pRef
     * @param defaultValue
     * @return String
     */
    fun getIntegerPrefrence(pRef: String?, defaultValue: Int): Int {
        return if (pref != null) pref!!.getInt(pRef, defaultValue) else -1
    }

    /**
     *
     * @param pRef
     * @param value
     */
    fun saveIntegerLongPrefrence(pRef: String?, value: Long) {
        try {
            val editor = pref?.edit()
            editor?.putLong(pRef, value)
            editor?.apply()
        } catch (e: Exception) {
        }
    }

    /**
     *
     * @param pRef
     * @param defaultValue
     * @return String
     */
    fun getLongPrefrence(pRef: String?, defaultValue: Long): Long {
        return if (pref != null) pref!!.getLong(pRef, defaultValue) else -1
    }

    companion object {
        private const val CURRENT_BLUETOOTH_NAME = "CURRENT_BLUETOOTH_NAME"
    }

    init {
        context = pContext
        pref = context?.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }
}