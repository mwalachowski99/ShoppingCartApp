package com.example.shoppingchartapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.shoppingchartapp.databinding.ActivityShopMapBinding
import com.example.shoppingchartapp.shop.ShopAdapter
import com.example.shoppingchartapp.shop.ShopViewModel
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager



class ShopMapActivity : AppCompatActivity() {
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var binding: ActivityShopMapBinding
    private lateinit var locationManager: LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shopListButton.setOnClickListener {
            startActivity(Intent(this, ShopListActivity::class.java))
        }

        binding.mapView.also {
            it.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS
            )
        }

        val shopViewModel = ShopViewModel(application)


        shopViewModel.allShops.observe(this, androidx.lifecycle.Observer {
            it.let{
                it.values.toList().forEach{ it -> addAnnotationToMap(it.name, it.latitude, it.longitude)}
            }
        })

        var permissionsListener: PermissionsListener = object : PermissionsListener {
            override fun onExplanationNeeded(permissionsToExplain: List<String>) {

            }

            override fun onPermissionResult(granted: Boolean) {
                if (granted) {

                    // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location

                } else {

                    // User denied the permission

                }
            }
        }

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location

        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun addAnnotationToMap(title: String, latitude: Double, longitude: Double){
        val pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()
        val marker = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
        val scaledMarker = Bitmap.createScaledBitmap(marker, (marker.width*0.3).toInt(), (marker.height*0.3).toInt(), true)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),1)
            return
        }

        if(longitude != null && latitude != null){
            val paOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(scaledMarker)
                .withTextAnchor(TextAnchor.TOP)
                .withTextField(title)

            pointAnnotationManager.create(paOptions)
        }else{
            Toast.makeText(this, "Location is null!", Toast.LENGTH_SHORT).show()
        }

    }
}