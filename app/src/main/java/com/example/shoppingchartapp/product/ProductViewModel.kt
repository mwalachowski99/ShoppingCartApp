package com.example.shoppingchartapp.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val allProducts: MutableLiveData<HashMap<String, Product>>

    init{
        repository = ProductRepository(firebaseDatabase)
        allProducts = repository.allProducts
    }

    suspend fun insert(product: Product) = repository.insert(product)

    suspend fun update(product: Product) = repository.update(product)

    suspend fun delete(product: Product) = repository.delete(product)
}