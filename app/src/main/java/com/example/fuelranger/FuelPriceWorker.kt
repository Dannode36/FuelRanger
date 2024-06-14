package com.example.fuelranger

import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FuelApiInterface {
    @GET("stations/{code}")
    fun getStationPrices(@Path("code") code: Int): Call<StationPrices>

    @GET("stations/radius")
    fun getStationPricesInRadius(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double,
                                 @Query("radius") radius: Double, @Query("fuelType") fuelType: String = "Any"): Call<List<StationPrices>>
}
class FuelPriceWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.20.72:5179/")
            .build()

        try {
            val service = retrofit.create(FuelApiInterface::class.java)
            val response = service.getStationPricesInRadius(-33.505830, 151.331460, 10000.0).execute().body()!!
        }
        catch (e: Exception){
            Result.retry()
        }

        return Result.success()
    }
}
