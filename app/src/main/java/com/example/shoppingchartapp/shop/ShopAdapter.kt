package com.example.shoppingchartapp.shop

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingchartapp.databinding.ShopListElementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ShopAdapter(private val shopViewModel: ShopViewModel) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    class ViewHolder(val binding: ShopListElementBinding) : RecyclerView.ViewHolder(binding.root)

    private var shops = emptyList<Shop>()
    private lateinit var prefs: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ShopListElementBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        prefs = holder.binding.root.context.getSharedPreferences("prefs", MODE_PRIVATE)

        holder.binding.textViewName.text = shops[position].name
        holder.binding.textViewDescription.text = shops[position].description
        holder.binding.textViewRadius.text = shops[position].radius.toString()

        val color = Color.rgb(prefs.getInt("Red", 0), prefs.getInt("Green", 0), prefs.getInt("Blue", 0))
        val textSize = prefs.getInt("Size", 24)

        holder.binding.textViewName.textSize = textSize.toFloat()
        holder.binding.textViewDescription.textSize = textSize.toFloat()
        holder.binding.textViewRadius.textSize = textSize.toFloat()

        holder.binding.textViewName.setTextColor(color)
        holder.binding.textViewDescription.setTextColor(color)
        holder.binding.textViewRadius.setTextColor(color)

        holder.binding.root.setOnLongClickListener{
            delete(shops[position])
            Toast.makeText(
                holder.binding.root.context,
                "Successfully removed shop with id: ${shops[position].id}",
                Toast.LENGTH_LONG
            ).show()
            true
        }
    }

    override fun getItemCount(): Int = shops.size

    fun add(shop: Shop){
        CoroutineScope(IO).launch {
            shopViewModel.insert(shop)
        }
        notifyDataSetChanged()
    }

    fun delete(shop: Shop){
        CoroutineScope(IO).launch {
            shopViewModel.delete(shop)
        }
        notifyDataSetChanged()
    }

    fun setShops(allShops: List<Shop>) {
        shops = allShops
        notifyDataSetChanged()
    }
}