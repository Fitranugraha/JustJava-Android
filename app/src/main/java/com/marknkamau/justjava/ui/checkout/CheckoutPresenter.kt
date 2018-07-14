package com.marknkamau.justjava.ui.checkout

import android.util.Base64
import com.marknkamau.justjava.data.network.authentication.AuthenticationService
import com.marknkamau.justjava.data.local.CartDao
import com.marknkamau.justjava.data.local.PreferencesRepository
import com.marknkamau.justjava.data.network.db.DatabaseService
import com.marknkamau.justjava.data.models.OrderItem
import com.marknkamau.justjava.data.models.Order
import com.marknkamau.justjava.ui.BasePresenter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import com.marknkamau.justjava.utils.mpesa.Mpesa

internal class CheckoutPresenter(private val activityView: CheckoutView,
                                 private val auth: AuthenticationService,
                                 private val preferences: PreferencesRepository,
                                 private val database: DatabaseService,
                                 private val mpesa: Mpesa,
                                 private val cart: CartDao) : BasePresenter() {
    fun getSignInStatus() {
        if (auth.isSignedIn()) {
            activityView.setDisplayToLoggedIn(preferences.getUserDetails())
        } else {
            activityView.setDisplayToLoggedOut()
        }
    }

    fun placeOrder(orderId: String, address: String, comments: String) {
        val order = Order(orderId, auth.getUserId()!!, 0, 0, address, comments)
        activityView.showUploadBar()

        disposables.add(cart.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { items: MutableList<OrderItem> ->
                            placeOrderInternal(items, order)
                        },
                        onError = { t: Throwable? ->
                            Timber.e(t)
                        }
                ))

    }

    fun makeMpesaPayment(total: Int, phoneNumber: String, orderId: String) {

        mpesa.sendStkPush(total, phoneNumber, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { lnmPaymentResponse ->
                            activityView.displayMessage(lnmPaymentResponse.customerMessage)
                        },
                        { t ->
                            Timber.e(t)
                            activityView.displayMessage(t.message)
                        }
                )

//        val keys = "$consumerKey:$consumerSecret"
//        val accessTokenHeader = "Basic ${Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)}"
//
//        mpesaService.getAccessToken(accessTokenHeader)
//                .flatMap { oAuthAccess: OAuthAccess ->
//                    val token = FirebaseInstanceId.getInstance().token
//                    val timestamp = Utils.timestampNow
//                    val stkPush = STKPush(
//                            MpesaConfig.BUSINESS_SHORT_CODE,
//                            Utils.getPassword(MpesaConfig.BUSINESS_SHORT_CODE, MpesaConfig.PASSKEY, timestamp),
//                            Utils.timestampNow,
//                            MpesaConfig.TRANSACTION_TYPE,
//                            total,
//                            Utils.sanitizePhoneNumber(phoneNumber),
//                            MpesaConfig.PARTY_B,
//                            Utils.sanitizePhoneNumber(phoneNumber),
//                            MpesaConfig.CALLBACK_URL + token,
//                            orderId, //The account reference
//                            "Payment for order: $orderId") //The transaction description
//
//                    val lnmHeader = "Bearer ${oAuthAccess.accessToken}"
//
//                    mpesaService.sendPush(lnmHeader, stkPush)
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        { lnmPaymentResponse ->
//                            activityView.displayMessage(lnmPaymentResponse.customerMessage)
//                        },
//                        { t ->
//                            Timber.e(t)
//                            activityView.displayMessage(t.message)
//                        }
//                )
    }

    private fun placeOrderInternal(items: MutableList<OrderItem>, order: Order) {
        val itemsCount = items.size
        var total = 0
        items.forEach { item -> total += item.itemPrice }

        order.itemsCount = itemsCount
        order.totalPrice = total

        database.placeNewOrder(order, items, object : DatabaseService.WriteListener {
            override fun onSuccess() {
                disposables.add(Completable.fromCallable { cart.deleteAll() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onComplete = {
                                    activityView.hideUploadBar()
                                    activityView.displayMessage("Order placed")
                                    activityView.finishActivity()
                                },
                                onError = { t: Throwable? ->
                                    Timber.e(t)
                                    activityView.hideUploadBar()
                                    activityView.displayMessage(t?.message)
                                }
                        ))

            }

            override fun onError(reason: String) {
                activityView.hideUploadBar()
                activityView.displayMessage(reason)
            }
        })

    }
}
