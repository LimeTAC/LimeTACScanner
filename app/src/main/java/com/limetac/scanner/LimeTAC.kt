package com.limetac.scanner

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.limetac.scanner.utils.Constants

class LimeTAC : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        if (BuildConfig.DEBUG)
            Constants.Environment.CURRENT_ENVIRONMENT = "https://devtgs.limetac.com"
        else
            Constants.Environment.CURRENT_ENVIRONMENT = "https://rctgsmobile.limetac.com"
    }
}