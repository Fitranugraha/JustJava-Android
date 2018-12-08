package com.marknkamau.justjavastaff.ui.orders

import com.marknjunge.core.data.firebase.StaffDatabaseService
import com.marknjunge.core.model.Order
import com.marknjunge.core.model.OrderStatus
import com.marknkamau.justjavastaff.data.local.SettingsRespository

import timber.log.Timber

internal class OrdersFragmentPresenter(private val view: OrdersView, private val settings: SettingsRespository, private val databaseService: StaffDatabaseService) {

    fun getOrders() {
        databaseService.getOrders(object : StaffDatabaseService.OrdersListener {
            override fun onSuccess(orders: List<Order>) {
                if (orders.isEmpty()) {
                    view.displayNoOrders()
                } else {
                    val statusSettings = settings.getStatusSettings()
                    val filteredOrders = mutableListOf<Order>()
                    orders.forEach {
                        if (it.status == OrderStatus.PENDING && statusSettings.pending)
                            filteredOrders.add(it)
                        if (it.status == OrderStatus.INPROGRESS && statusSettings.inProgress)
                            filteredOrders.add(it)
                        if (it.status == OrderStatus.COMPLETED && statusSettings.completed)
                            filteredOrders.add(it)
                        if (it.status == OrderStatus.DELIVERED && statusSettings.delivered)
                            filteredOrders.add(it)
                        if (it.status == OrderStatus.CANCELLED && statusSettings.cancelled)
                            filteredOrders.add(it)
                    }
                    view.displayAvailableOrders(filteredOrders)
                }
            }

            override fun onError(reason: String) {
                view.displayMessage(reason)
            }

        })

    }
}
