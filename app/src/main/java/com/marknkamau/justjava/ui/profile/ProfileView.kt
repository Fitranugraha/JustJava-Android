package com.marknkamau.justjava.ui.profile

import com.marknkamau.justjava.data.models.Order
import com.marknkamau.justjava.data.models.UserDetails

internal interface ProfileView {
    fun displayUserDetails(userDetails: UserDetails)
    fun showProgressBar()
    fun hideProgressBar()
    fun displayNoPreviousOrders()
    fun displayPreviousOrders(orderList: MutableList<Order>)
    fun displayMessage(message: String?)
}
