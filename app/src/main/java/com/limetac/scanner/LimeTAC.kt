package com.limetac.scanner

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.Preference

class LimeTAC : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        if (BuildConfig.DEBUG) {
            Constants.Environment.CURRENT_ENVIRONMENT = "https://devtgs.limetac.com"
        } else {
            Constants.Environment.CURRENT_ENVIRONMENT = "https://rctgsmobile.limetac.com"
        }

        val preference = Preference(this)
        if (preference.getStringPrefrence(Constants.PreferenceKeys.CURRENT_ENVIRONMENT_KEY, "").isNullOrEmpty()) {
            preference.saveStringInPrefrence(Constants.PreferenceKeys.CURRENT_ENVIRONMENT_KEY, "https://rctgsmobile.limetac.com")
            Constants.Environment.CURRENT_ENVIRONMENT = "https://rctgsmobile.limetac.com"
        } else {
            preference.getStringPrefrence(Constants.PreferenceKeys.CURRENT_ENVIRONMENT_KEY, "")
                ?.let {
                    Constants.Environment.CURRENT_ENVIRONMENT = it
                }
        }
    }
}