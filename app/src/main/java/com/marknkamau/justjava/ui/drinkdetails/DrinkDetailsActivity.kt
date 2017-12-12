package com.marknkamau.justjava.ui.drinkdetails

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.marknkamau.justjava.JustJavaApp

import com.marknkamau.justjava.R
import com.marknkamau.justjava.models.CartItemRoom
import com.marknkamau.justjava.models.CoffeeDrink
import com.marknkamau.justjava.ui.BaseActivity
import com.marknkamau.justjava.ui.main.CatalogAdapter
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_drink_details.*

class DrinkDetailsActivity : BaseActivity(), DrinkDetailsView, View.OnClickListener {
    private lateinit var drink: CoffeeDrink
    private var quantity: Int = 0
    private lateinit var presenter: DrinkDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cartDao = (application as JustJavaApp).cartDatabase.cartDao()

        presenter = DrinkDetailsPresenter(this, cartDao)

        drink = intent.extras.getParcelable<CoffeeDrink>(CatalogAdapter.DRINK_KEY)

        tvDrinkName.text = drink.drinkName
        tvDrinkContents.text = drink.drinkContents
        tvDrinkDescription.text = drink.drinkDescription
        tvDrinkPrice.text = String.format("%s%s", resources.getString(R.string.ksh), drink.drinkPrice)
        tvSubtotal.text = String.format("%s%s", resources.getString(R.string.ksh), drink.drinkPrice)

        val drinkImage = "file:///android_asset/" + drink.drinkImage
        val picasso = Picasso.with(this)
        picasso.load(drinkImage).noFade().into(imgDrinkImage)

        quantity = 1

        imgMinusQty.setOnClickListener(this)
        imgAddQty.setOnClickListener(this)
        btnAddToCart.setOnClickListener(this)
        cbChocolate.setOnClickListener(this)
        cbCinnamon.setOnClickListener(this)
        cbMarshmallow.setOnClickListener(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.unSubscribe()
    }

    override fun onClick(view: View) {
        when (view) {
            imgMinusQty -> minusQty()
            imgAddQty -> addQty()
            btnAddToCart -> addToCart()
            cbCinnamon -> updateSubtotal()
            cbChocolate -> updateSubtotal()
            cbMarshmallow -> updateSubtotal()
        }
    }

    override fun displayMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishActivity() {
        finish()
    }

    private fun addToCart() {
        val itemRm = CartItemRoom(0,
                drink.drinkName,
                quantity,
                cbCinnamon.isChecked,
                cbChocolate.isChecked,
                cbMarshmallow.isChecked,
                updateSubtotal()
                )

        presenter.addToCart(itemRm)
    }

    private fun minusQty() {
        if (quantity > 1) {
            quantity -= 1
        }
        tvQuantity.text = quantity.toString()
        updateSubtotal()
    }

    private fun addQty() {
        quantity += 1
        tvQuantity.text = quantity.toString()
        updateSubtotal()
    }

    private fun updateSubtotal(): Int {
        var base = Integer.parseInt(drink.drinkPrice)
        base *= quantity
        if (cbCinnamon.isChecked) {
            base += quantity * 100
        }
        if (cbChocolate.isChecked) {
            base += quantity * 100
        }
        if (cbMarshmallow.isChecked) {
            base += quantity * 100
        }
        tvSubtotal.text = resources.getString(R.string.ksh) + base
        return base
    }
}
