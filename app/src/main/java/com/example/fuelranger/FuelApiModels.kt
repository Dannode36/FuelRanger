package com.example.fuelranger

data class StationPrices(
    var station: Station = Station(),
    var prices: MutableList<FuelPrice> = mutableListOf()
)

data class Station(
    var brandId: String = "",
    var stationId: String = "",
    var brand: String = "",
    var code: String = "",
    var name: String = "",
    var address: String = "",
    var location: Location = Location(0.0, 0.0),
    var isAdBlueAvailable: Boolean = false
)

data class FuelPrice(
    var stationCode: String,
    var fuelType: String,
    var price: Double,
    var lastUpdated: String
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
