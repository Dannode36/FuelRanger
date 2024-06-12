package com.example.fuelranger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fuelranger.ui.theme.FuelRangerTheme
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("stations/{code}")
    fun getStationPrices(@Path("code") code: Int?): Call<ResponseBody>?
}

class MainActivity : ComponentActivity() {
    var name = ""
    private fun setUiContent(){
        setContent {
            FuelRangerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = name,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.20.72:5179/")
            .build()

        val service = retrofit.create(GitHubService::class.java)
        service.getStationPrices(972)?.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                println(t.message)
            }

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                name = response.body()?.string() ?: ""
                setUiContent()
                println("hoho")
            }
        })

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setUiContent()
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