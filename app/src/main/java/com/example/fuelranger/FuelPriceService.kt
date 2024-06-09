package com.example.fuelranger

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class FuelPriceService : Service() {
    private var startMode: Int = 0             // indicates how to behave if the service is killed
    private var binder: FuelPriceBinder = FuelPriceBinder()        // interface for clients that bind
    private var allowRebind: Boolean = false   // indicates whether onRebind should be used

    override fun onCreate() {
        // The service is being created
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // The service is starting, due to a call to startService()
        return startMode
    }

    override fun onBind(intent: Intent): FuelPriceBinder {
        // A client is binding to the service with bindService()
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // All clients have unbound with unbindService()
        return allowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    override fun onDestroy() {
        // The service is no longer used and is being destroyed
    }
    inner class FuelPriceBinder : Binder() {
        fun getService(): FuelPriceService = this@FuelPriceService
        fun GetFuelPricesWithinRadius(fuelType: String): StationPrices = StationPrices()
    }
}
