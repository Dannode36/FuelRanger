package com.example.fuelranger

import com.google.gson.annotations.SerializedName

data class StationPrices(
    @SerializedName("station")
    val station: Station = Station(),
    @SerializedName("prices")
    val prices: MutableList<FuelPrice> = mutableListOf()
)

data class Station(
    val brandId: String = "",
    val stationId: String = "",
    val brand: String = "",
    val code: String = "",
    val name: String = "",
    val address: String = "",
    val location: Location = Location(0.0, 0.0),
    val isAdBlueAvailable: Boolean = false
)

data class FuelPrice(
    @SerializedName("stationcode")
    val stationCode: String = "",
    @SerializedName("fueltype")
    val fuelType: String = "",
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("lastupdated")
    val lastUpdated: String = ""
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
