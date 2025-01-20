package com.example.shoppingchartapp

import android.Manifest.permission.*
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.shoppingchartapp.databinding.ActivityMainBinding
import com.example.shoppingchartapp.shop.ShopViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

private const val REQ_LOCATION_PERMISSION = 1
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var geoClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geoClient = LocationServices.getGeofencingClient(this)

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        binding.shoppingChartButton.setOnClickListener{
            startActivity(Intent(this, ShoppingListActivity::class.java))
        }

        binding.settingsButton.setOnClickListener{
            startActivity(Intent(this, OptionsActivity::class.java))
        }

        binding.accountButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.shopMapButton.setOnClickListener {
            startActivity(Intent(this, ShopMapActivity::class.java))
        }

        val shopViewModel = ShopViewModel(application)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            Intent(this, GeoReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        geoClient.removeGeofences(pendingIntent)

        shopViewModel.allShops.observe(this, androidx.lifecycle.Observer {
            it.let{
                it.values.toList().forEach{ it ->
                    val geofenceEnter = Geofence.Builder()
                        .setCircularRegion(it.latitude, it.longitude, it.radius)
                        .setExpirationDuration(30*60*1000)
                        .setRequestId("geoEnter${it.name}")
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build()

                    val geofenceExit = Geofence.Builder()
                        .setCircularRegion(it.latitude, it.longitude, it.radius)
                        .setExpirationDuration(30*60*1000)
                        .setRequestId("geoExit${it.name}")
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build()

                    val geoRequest = GeofencingRequest.Builder()
                        .addGeofences(listOf(geofenceEnter, geofenceExit))
                        .build()

                    geoClient.addGeofences(geoRequest, pendingIntent)
                        .addOnSuccessListener {
                            Log.i("geofenceApp", "Geofence: ${geofenceEnter.requestId}  is added!")
                        }
                        .addOnFailureListener { it ->
                            Log.e("geofenceApp", it.message.toString())
                        }
                }
            }
        })


    }

    private fun checkPermission(){
        val permissionStatusLocation = checkSelfPermission(ACCESS_FINE_LOCATION)
        val permissionStatusBackgroundLocation = checkSelfPermission(ACCESS_BACKGROUND_LOCATION)
        if(permissionStatusLocation != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION), REQ_LOCATION_PERMISSION)
        }
        if(permissionStatusBackgroundLocation != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_BACKGROUND_LOCATION), REQ_LOCATION_PERMISSION)
        }
    }
}