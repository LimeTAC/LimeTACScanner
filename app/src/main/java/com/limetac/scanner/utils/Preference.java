package com.limetac.scanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private String prefName = "limetac_prefs";

        private Context context = null;
        private SharedPreferences pref = null;

        private static String CURRENT_BLUETOOTH_NAME="CURRENT_BLUETOOTH_NAME";

        public Preference(final Context pContext) {
            this.context = pContext;
            pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }

        /**
         *
         * @param pRef
         * @param value
         */
        public void saveStringInPrefrence(final String pRef, final String value) {

            try {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(pRef, value);
                editor.apply();
            } catch (Exception e) {

            }
        }

        public void saveBooleanFlagInPrefrence(final String key, final boolean value) {

            try {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(key, value);
                editor.apply();
            } catch (Exception e) {

            }
        }

        public boolean getBooleanFlagPrefrence(final String key, final boolean defaultValue) {
            return pref.getBoolean(key, defaultValue);
        }

        /**
         *
         * @param pRef
         * @param defaultValue
         * @return String
         */
        public String getStringPrefrence(final String pRef,
                                         final String defaultValue) {
            return pref.getString(pRef, defaultValue);
        }

        /**
         *
         * @param pRef
         * @param value
         */
        public void saveIntegerInPrefrence(final String pRef, final int value) {

            try {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(pRef, value);
                editor.apply();
            } catch (Exception e) {

            }
        }

        /**
         *
         * @param pRef
         * @param defaultValue
         * @return String
         */
        public int getIntegerPrefrence(final String pRef, final int defaultValue) {
            return pref.getInt(pRef, defaultValue);
        }

        /**
         *
         * @param pRef
         * @param value
         */
        public void saveIntegerLongPrefrence(final String pRef, final long value) {

            try {
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong(pRef, value);
                editor.apply();
            } catch (Exception e) {

            }
        }

        /**
         *
         * @param pRef
         * @param defaultValue
         * @return String
         */
        public long getLongPrefrence(final String pRef, final long defaultValue) {
            return pref.getLong(pRef, defaultValue);
        }



}
