package com.example.shoppingchartapp.shop

import java.io.Serializable

data class Shop(
    var id: String,
    var name: String,
    var description: String,
    var radius: Float,
    var latitude: Double,
    var longitude: Double
    ) : Serializable