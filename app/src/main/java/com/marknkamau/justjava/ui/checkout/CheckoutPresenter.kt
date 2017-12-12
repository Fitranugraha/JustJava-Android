package com.marknkamau.justjava.ui.checkout

import com.marknkamau.justjava.authentication.AuthenticationService
import com.marknkamau.justjava.data.local.CartDao
import com.marknkamau.justjava.models.Order
import com.marknkamau.justjava.data.local.PreferencesRepository
import com.marknkamau.justjava.models.CartItem
import com.marknkamau.justjava.data.network.DatabaseService
import com.marknkamau.justjava.ui.BasePresenter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

internal class CheckoutPresenter(private val activityView: CheckoutView,
                                 val auth: AuthenticationService,
                                 val preferences: PreferencesRepository,
                                 val database: DatabaseService,
                                 private val cart: CartDao) : BasePresenter() {
    fun getSignInStatus() {
        if (auth.isSignedIn()) {
            activityView.setDisplayToLoggedIn(preferences.getDefaults())
        } else {
            activityView.setDisplayToLoggedOut()
        }
    }

    fun placeOrder(order: Order) {
        activityView.showUploadBar()

        disposables.add(cart.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { items: MutableList<CartItem> ->
                            placeOrderInternal(items, order)
                        },
                        onError = { t: Throwable? ->
                            Timber.e(t)
                        }
                ))

    }

    private fun placeOrderInternal(items: MutableList<CartItem>, order: Order) {
        val itemsCount = items.size
        var total = 0
        items.forEach { item -> total += item.itemPrice }

        order.itemsCount = itemsCount
        order.totalPrice = total

        database.placeNewOrder(order, items, object : DatabaseService.UploadListener {
            override fun taskSuccessful() {
                val deleteAll = Completable.fromCallable { cart.deleteAll() }
                val resetIndex = Completable.fromCallable { cart.resetIndex() }

                val merged = deleteAll.mergeWith(resetIndex)

                disposables.add(merged
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onComplete = {
                                    activityView.hideUploadBar()
                                    activityView.displayMessage("Order placed")
                                    activityView.finishActivity()
                                },
                                onError = {
                                    t: Throwable? ->
                                    Timber.e(t)
                                    activityView.hideUploadBar()
                                    activityView.displayMessage(t?.message)
                                }
                        ))

            }

            override fun taskFailed(reason: String?) {
                activityView.hideUploadBar()
                activityView.displayMessage(reason)
            }
        })

    }
}
