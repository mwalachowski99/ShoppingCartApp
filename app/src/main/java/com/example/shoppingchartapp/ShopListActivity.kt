package com.example.shoppingchartapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingchartapp.databinding.ActivityShopListBinding
import com.example.shoppingchartapp.shop.Shop
import com.example.shoppingchartapp.shop.ShopAdapter
import com.example.shoppingchartapp.shop.ShopViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class ShopListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopListBinding
    private lateinit var geoClient: GeofencingClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geoClient = LocationServices.getGeofencingClient(this)

        val shopViewModel = ShopViewModel(application)

        val adapter = ShopAdapter(shopViewModel)

        binding.shopsRv.layoutManager = LinearLayoutManager(this)
        binding.shopsRv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.shopsRv.adapter = adapter
        shopViewModel.allShops.observe(this, androidx.lifecycle.Observer {
            it.let{
                adapter.setShops(it.values.toList())
            }
        })

        binding.addButton.setOnClickListener{
            addButtonClicked(adapter)
        }
    }
    private fun addButtonClicked(adapter: ShopAdapter){

        val name = binding.editTextName.text.toString()
        val description =  binding.editTextDescription.text.toString()
        val radius = binding.editTextRadius.text.toString().toFloatOrNull() ?: 10f
        if(name.isEmpty()){
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_LONG).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), 1
            )
        }
        LocationServices.getFusedLocationProviderClient(this).lastLocation
            .addOnSuccessListener {
                if(it==null){
                    Toast.makeText(this, "Location is null" , Toast.LENGTH_LONG).show()
                }else{
                    adapter.add(
                        Shop(
                            id = "",
                            name = name,
                            description = description,
                            radius = radius,
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                    )

                    Toast.makeText(this, "Added new shop: $name" , Toast.LENGTH_LONG).show()

                    addGeo(it, name, radius)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Location error: ${it.message.toString()}" , Toast.LENGTH_LONG).show()
            }
        }
        @SuppressLint("MissingPermission")
        private fun addGeo(location: Location, name: String, radius: Float){
            val geofenceEnter = Geofence.Builder()
                .setCircularRegion(location.latitude, location.longitude, radius)
                .setExpirationDuration(30*60*1000)
                .setRequestId("geoEnter$name")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            val geofenceExit = Geofence.Builder()
                .setCircularRegion(location.latitude, location.longitude, radius)
                .setExpirationDuration(30*60*1000)
                .setRequestId("geoExit$name")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

            val geoRequest = GeofencingRequest.Builder()
                .addGeofences(listOf(geofenceEnter, geofenceExit))
                .build()

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                Intent(this, GeoReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            geoClient.addGeofences(geoRequest, pendingIntent)
                .addOnSuccessListener {
                    Log.i("geofenceApp", "Geofence: ${geofenceEnter.requestId}  is added!")
                }
                .addOnFailureListener {
                    Log.e("geofenceApp", it.message.toString())
                }
        }
    }
