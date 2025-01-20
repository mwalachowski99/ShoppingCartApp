package com.example.shoppingchartapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingchartapp.databinding.ActivityShoppingListBinding
import com.example.shoppingchartapp.product.Product
import com.example.shoppingchartapp.product.ProductAdapter
import com.example.shoppingchartapp.product.ProductViewModel
import java.util.*

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("prefs", MODE_PRIVATE)

        val productViewModel = ProductViewModel(application)

        val adapter = ProductAdapter(productViewModel)

        val currency = Currency.getInstance(prefs.getString("Currency", "PLN"))

        binding.textViewCurrency.text = currency.symbol
        binding.productsRv.layoutManager = LinearLayoutManager(this)
        binding.productsRv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.productsRv.adapter = adapter
        productViewModel.allProducts.observe(this, androidx.lifecycle.Observer {
            it.let{
                adapter.setProducts(it.values.toList())
            }
        })

        binding.addButton.setOnClickListener{
            addButtonClicked(adapter)
        }
    }

    private fun addButtonClicked(adapter: ProductAdapter){

        val currencyCode = prefs.getString("Currency", "PLN")
        val name = binding.editTextName.text.toString()
        val quantity =  binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
        val price = binding.editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
        if(name.isEmpty()){
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_LONG).show()
            return
        }

         adapter.add(
            Product(
                id = "",
                name = name,
                quantity = quantity,
                price = price * CurrencyExchangeRates.valueOf(currencyCode?:"PLN").rate,
                bought = false
            )
        )

        Toast.makeText(this, "Added new product: $name" , Toast.LENGTH_LONG).show()
    }
}