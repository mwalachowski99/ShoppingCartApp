package com.example.shoppingchartapp.shop

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ShopRepository(private val fbdb: FirebaseDatabase) {

    val allShops: MutableLiveData<HashMap<String, Shop>> =
        MutableLiveData<HashMap<String, Shop>>().also{
            it.value = HashMap<String, Shop>()
        }

    init{
        fbdb.getReference("Shop").addChildEventListener(
            object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val shop = Shop(
                        id = snapshot.ref.key as String,
                        name = snapshot.child("name").getValue(String::class.java)!!,
                        description = snapshot.child("description").getValue(String::class.java)!!,
                        radius = snapshot.child("radius").getValue(Float::class.java)!!,
                        latitude = snapshot.child("latitude").getValue(Double::class.java)!!,
                        longitude = snapshot.child("longitude").getValue(Double::class.java)!!,
                    )
                    allShops.value?.put(shop.id, shop)
                    allShops.postValue(allShops.value)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    allShops.value?.remove(snapshot.ref.key)
                    allShops.postValue(allShops.value)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )
    }

    fun insert(shop: Shop){
        fbdb.getReference("Shop").push().also{
            shop.id = it.ref.key.toString()
            it.setValue(shop)
        }
    }


    fun delete(shop: Shop) = fbdb.getReference("Shop/${shop.id}").removeValue()

}