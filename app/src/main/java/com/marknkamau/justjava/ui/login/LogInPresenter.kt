package com.marknkamau.justjava.ui.login

import android.os.Handler
import com.marknkamau.justjava.data.PreferencesRepository
import com.marknkamau.justjava.models.UserDefaults
import com.marknkamau.justjava.authentication.AuthenticationService

import com.marknkamau.justjava.network.DatabaseService
import com.marknkamau.justjava.network.DatabaseServiceImpl

internal class LogInPresenter(private val activityView: LogInView, private val preferences: PreferencesRepository, private val auth: AuthenticationService) {

    fun checkSignInStatus() {
        if (auth.getCurrentUser() != null) {
            activityView.closeActivity()
        }
    }

    fun signIn(email: String, password: String) {
        activityView.showDialog()
        auth.signIn(email, password, object : AuthenticationService.AuthActionListener {
            override fun actionSuccessful(response: String?) {
                getUserDefaults()
            }

            override fun actionFailed(response: String?) {
                activityView.displayMessage(response)
            }
        })
    }

    private fun getUserDefaults() {
        DatabaseServiceImpl.getUserDefaults(object : DatabaseService.UserDetailsListener {
            override fun taskSuccessful(name: String, phone: String, deliveryAddress: String) {
                preferences.saveDefaults(UserDefaults(name, phone, deliveryAddress))
                activityView.dismissDialog()
                activityView.displayMessage("Sign in successful")
                Handler().postDelayed({ activityView.finishSignUp() }, 500)
            }

            override fun taskFailed(reason: String?) {
                activityView.displayMessage(reason)
            }
        })
    }

    fun resetUserPassword(email: String) {
        auth.sendPasswordResetEmail(email, object : AuthenticationService.AuthActionListener {
            override fun actionSuccessful(response: String?) {
                activityView.displayMessage(response)
            }

            override fun actionFailed(response: String?) {
                activityView.displayMessage(response)
            }
        })
    }
}
