package com.example.fuelranger

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.fuelranger.ui.theme.FuelRangerTheme
import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private val fuelTypes = listOf("E10", "P91", "P95", "P98")

    override fun onCreate(savedInstanceState: Bundle?) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<FuelPriceWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "FuelPriceWorker",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RuntimePermissionsDialog(
                Manifest.permission.POST_NOTIFICATIONS,
                onPermissionDenied = {},
                onPermissionGranted = {},
            )

            FuelRangerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Under construction...",
                        modifier = Modifier.padding(innerPadding)
                    )
                    FuelTypeCheckboxList(fuelTypes)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FuelRangerTheme {
        Greeting("Android")
    }
}

@Composable
fun FuelTypeCheckboxList(fuelTypes: List<String>) {

    Column {
        for (type in fuelTypes){
            var checked by remember { mutableStateOf(true) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(type)
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
            }
        }
    }
}

@Composable
fun RuntimePermissionsDialog(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {

    if (ContextCompat.checkSelfPermission(
            LocalContext.current,
            permission) != PackageManager.PERMISSION_GRANTED) {

        val requestLocationPermissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->

                if (isGranted) {
                    onPermissionGranted()
                } else {
                    onPermissionDenied()
                }
            }

        SideEffect {
            requestLocationPermissionLauncher.launch(permission)
        }
    }
}