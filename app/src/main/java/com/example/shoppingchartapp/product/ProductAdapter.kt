package com.example.shoppingchartapp.product

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingchartapp.CurrencyExchangeRates
import com.example.shoppingchartapp.EditActivity
import com.example.shoppingchartapp.databinding.ProductListElementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class ProductAdapter(private val productViewModel: ProductViewModel) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

        class ViewHolder(val binding: ProductListElementBinding) : RecyclerView.ViewHolder(binding.root)

    private var products = emptyList<Product>()
    private lateinit var prefs: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductListElementBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        prefs = holder.binding.root.context.getSharedPreferences("prefs", MODE_PRIVATE)

        val currencyCode = prefs.getString("Currency", "PLN")
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(currencyCode)

        holder.binding.textViewName.text = products[position].name
        holder.binding.textViewPrice.text = format.format(products[position].price * (1/ CurrencyExchangeRates.valueOf(currencyCode?:"PLN").rate))
        holder.binding.textViewQuantity.text = products[position].quantity.toString()
        holder.binding.checkBoxBought.isChecked = products[position].bought

        val color = Color.rgb(prefs.getInt("Red", 0), prefs.getInt("Green", 0), prefs.getInt("Blue", 0))
        val textSize = prefs.getInt("Size", 24)

        holder.binding.textViewName.textSize = textSize.toFloat()
        holder.binding.textViewPrice.textSize = textSize.toFloat()
        holder.binding.textViewQuantity.textSize = textSize.toFloat()
        holder.binding.checkBoxBought.textSize = textSize.toFloat()

        holder.binding.textViewName.setTextColor(color)
        holder.binding.textViewPrice.setTextColor(color)
        holder.binding.textViewQuantity.setTextColor(color)
        holder.binding.checkBoxBought.setTextColor(color)




        holder.binding.root.setOnLongClickListener{
            delete(products[position])
            Toast.makeText(
                holder.binding.root.context,
                "Successfully removed product with id: ${products[position].id}",
                Toast.LENGTH_LONG
            ).show()
            true
        }

        holder.binding.root.setOnClickListener{
            val intent = Intent(holder.binding.root.context, EditActivity::class.java)
            intent.putExtra("product", products[position])
            startActivity(
                holder.binding.root.context,
                intent,
                Bundle()
            )

        }

        holder.binding.checkBoxBought.setOnClickListener {
            products[position].bought = holder.binding.checkBoxBought.isChecked
            updateProduct(products[position])
        }
    }

    override fun getItemCount(): Int = products.size

    fun add(product: Product){
        CoroutineScope(IO).launch {
            productViewModel.insert(product)
        }
        notifyDataSetChanged()
    }

    fun delete(product: Product){
        CoroutineScope(IO).launch {
            productViewModel.delete(product)
        }
        notifyDataSetChanged()
    }

    fun setProducts(allProduct: List<Product>){
        products = allProduct
        notifyDataSetChanged()
    }

    fun updateProduct(product: Product){
        CoroutineScope(IO).launch {
            productViewModel.update(product)
        }
    }
}