package com.marknkamau.justjava

import android.app.Application
import android.arch.persistence.room.Room
import android.os.Build
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.marknkamau.justjava.authentication.AuthenticationService
import com.marknkamau.justjava.authentication.AuthenticationServiceImpl
import com.marknkamau.justjava.data.local.CartDatabase
import com.marknkamau.justjava.data.local.PreferencesRepository
import com.marknkamau.justjava.data.local.PreferencesRepositoryImpl
import com.marknkamau.justjava.data.network.DatabaseService
import com.marknkamau.justjava.data.network.DatabaseServiceImpl
import com.marknkamau.justjava.utils.NotificationHelper
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class JustJavaApp : Application() {
    lateinit var preferencesRepo: PreferencesRepository
    lateinit var authService: AuthenticationService
    lateinit var databaseService: DatabaseService
    lateinit var cartDatabase: CartDatabase
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "Timber ${super.createStackElementTag(element)}.${element.methodName}"
                }
            })
        } else {
            Fabric.with(this, Crashlytics())
        }

        preferencesRepo = PreferencesRepositoryImpl(PreferenceManager.getDefaultSharedPreferences(this))
        authService = AuthenticationServiceImpl
        databaseService = DatabaseServiceImpl()

        cartDatabase = Room.databaseBuilder(this, CartDatabase::class.java, "cart-db").build()

        notificationHelper = NotificationHelper(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper.createChannel(getString(R.string.default_notification_channel), "Default notification channel", "Default notifications" )
        }
    }

}
