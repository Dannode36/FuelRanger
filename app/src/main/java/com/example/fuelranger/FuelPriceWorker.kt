package com.example.fuelranger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.properties.Delegates
import android.Manifest
import android.util.Log
import androidx.work.Data
import androidx.work.ForegroundInfo
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

interface FuelApiInterface {
    @GET("stations/radius")
    fun getStationPricesInRadius(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double,
                                 @Query("radius") radius: Double, @Query("fuelType") fuelType: String = "Any"): Call<List<StationPrices>>
}
class FuelPriceWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {
    private val notificationChannelId = "fuelRangerPriceNotification"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.20.72:5179/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            "DemoWorker",
            NotificationManager.IMPORTANCE_LOW,
        )

        val notificationManager: NotificationManager? =
            getSystemService(
                applicationContext,
                NotificationManager::class.java)

        notificationManager?.createNotificationChannel(
            notificationChannel
        )
    }
    private fun createNotification(content: String) : Notification {
        createNotificationChannel()

        val mainActivityIntent = Intent(
            applicationContext,
            MainActivity::class.java)

        var pendingIntentFlag by Delegates.notNull<Int>()
        pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE

        val mainActivityPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            mainActivityIntent,
            pendingIntentFlag)

        return NotificationCompat.Builder(
            applicationContext,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(content)
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
    }

    fun GetStationPricesInRadius(): Response<List<StationPrices>> {
        val service = retrofit.create(FuelApiInterface::class.java)
        val response = service.getStationPricesInRadius(-33.505830, 151.331460, 10000.0, "P95").execute()
        if(!response.isSuccessful){
            error("API request failed. Status code: " + response.code())
        }
        println(response.toString())
        println("success")

        return response
    }

    override fun doWork(): Result {
        try {
            val service = retrofit.create(FuelApiInterface::class.java)
            val response = service.getStationPricesInRadius(-33.505830, 151.331460, 10000.0, "P95").execute()
            if(!response.isSuccessful){
                error("API request failed. Status code: " + response.code())
            }
            println(response.toString())
            println("success")

            val prices = response.body()!!

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                with(NotificationManagerCompat.from(applicationContext)) {
                    val price = prices[0].prices[0]
                    notify(0, createNotification(price.fuelType + ": " + price.price + " @ " + prices[0].station.name))
                }
            }
        }
        catch (e: Exception){
            Log.e("FuelPriceWorker", e.toString())

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(0, createNotification(e.toString()))
                }
            }

            return Result.failure()
        }



        return Result.success()
    }

    override fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            0, createNotification("foreground info")
        )
    }
}
