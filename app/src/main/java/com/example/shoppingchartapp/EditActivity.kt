package com.example.shoppingchartapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppingchartapp.databinding.ActivityEditBinding
import com.example.shoppingchartapp.product.Product
import com.example.shoppingchartapp.product.ProductAdapter
import com.example.shoppingchartapp.product.ProductViewModel
import java.util.*

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productViewModel = ProductViewModel(application)
        val adapter = ProductAdapter(productViewModel)

        prefs = getSharedPreferences("prefs", MODE_PRIVATE)

        val product = intent.extras?.get("product") as Product

        val currencyCode = prefs.getString("Currency", "PLN")
        val currency = Currency.getInstance(currencyCode)

        binding.editTextName.setText(product.name)
        binding.editTextQuantity.setText(product.quantity.toString())
        binding.editTextPrice.setText((product.price * (1/CurrencyExchangeRates.valueOf(currencyCode?:"PLN").rate)).toString())

        binding.textViewCurrency.text = currency.symbol

        binding.saveButton.setOnClickListener {
            saveButtonClicked(adapter, product)
        }

    }
    private fun saveButtonClicked(adapter: ProductAdapter, product: Product){
        val currencyCode = prefs.getString("Currency", "PLN")
        val name = binding.editTextName.text.toString()
        val quantity =  binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
        val price = binding.editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
        if(name.isEmpty()){
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_LONG).show()
            return
        }
        product.name = name
        product.quantity = quantity
        product.price = price * CurrencyExchangeRates.valueOf(currencyCode?:"PLN").rate

        adapter.updateProduct(product)

        startActivity(Intent(this, ShoppingListActivity::class.java))
    }
}