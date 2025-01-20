package com.example.shoppingchartapp.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val allShops: MutableLiveData<HashMap<String, Shop>>

    init{
        repository = ShopRepository(firebaseDatabase)
        allShops = repository.allShops
    }

    suspend fun insert(shop: Shop) = repository.insert(shop)

    suspend fun delete(shop: Shop) = repository.delete(shop)
}