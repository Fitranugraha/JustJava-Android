package com.marknkamau.justjava

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.marknjunge.core.data.local.PreferencesRepository
import com.marknjunge.core.data.repository.UsersRepository
import com.marknjunge.core.di.repositoriesModule
import com.marknkamau.justjava.data.network.FirebaseService
import com.marknkamau.justjava.di.appModule
import com.marknkamau.justjava.di.dbModule
import com.marknkamau.justjava.di.viewModelModule
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
open class JustJavaApp : Application() {
    private val preferencesRepository: PreferencesRepository by inject()
    private val usersRepository: UsersRepository by inject()
    private val firebaseService: FirebaseService by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "Timber ${super.createStackElementTag(element)}.${element.methodName}"
                }
            })
        } else {
            Sentry.init(BuildConfig.sentryDsn, AndroidSentryClientFactory(this))
        }

        startKoin {
            androidContext(this@JustJavaApp)
            modules(
                listOf(
                    appModule,
                    repositoriesModule,
                    viewModelModule,
                    dbModule
                )
            )
        }

        Places.initialize(this, getString(R.string.google_api_key))

        if (preferencesRepository.isSignedIn) {
            coroutineScope.launch {
                usersRepository.updateFcmToken(firebaseService.getFcmToken())
                usersRepository.getCurrentUser().collect {  }
            }
        }
    }

}
