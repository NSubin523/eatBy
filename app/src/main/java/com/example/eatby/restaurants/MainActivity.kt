package com.example.eatby.restaurants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatby.R
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
private const val API_KEY = "Ae8wUUA_onNAsTO6JHMOi8v21fpSZsTTNqd75ZdbYtMnxtiBJBSXEQgoKb060bZV2yo51HiyKpSDJHdNUcQL0tiTEXuW1QJr5zOKSDgsL26bd_ui5pXjUaSeIXlOYHYx"
private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val defaultLocation = "New York"
private const val limit = 40
private const val categoryAttribute = "restaurants,deli,bars"
private const val defaultSorting = "rating"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBar)
        val searchBtn = findViewById<ImageButton>(R.id.searchBtn)
        val userLocation = findViewById<EditText>(R.id.locationBar)
        val burgerFrame = findViewById<FrameLayout>(R.id.burgerFrame)
        val chineseFrame = findViewById<FrameLayout>(R.id.chineseFrame)
        val pizzaFrame = findViewById<FrameLayout>(R.id.pizzaFrame)
        val indianFrame = findViewById<FrameLayout>(R.id.indianFrame)
        val sandwich = findViewById<FrameLayout>(R.id.sandwichFrame)
        val deliFrame = findViewById<FrameLayout>(R.id.deliFrame)

        displayBodyOnCall("Restaurants", defaultLocation, "rating")

        searchBtn.setOnClickListener{
            displayBodyOnCall(searchBar.text.toString(),userLocation.text.toString(),
                defaultSorting)
        }

        burgerFrame.setOnClickListener{
            displayBodyOnCall("Burger", defaultLocation, defaultSorting)
        }

        chineseFrame.setOnClickListener{
            displayBodyOnCall("Chinese", defaultLocation, defaultSorting)
        }

        pizzaFrame.setOnClickListener{
            displayBodyOnCall("Pizza", defaultLocation, defaultSorting)
        }

        indianFrame.setOnClickListener{
            displayBodyOnCall("Indian", defaultLocation, defaultSorting)
        }

        sandwich.setOnClickListener{
            displayBodyOnCall("Sandwich", defaultLocation, defaultSorting)
        }

        deliFrame.setOnClickListener{
            displayBodyOnCall("Deli", defaultLocation, defaultSorting)
        }

    }
    private fun displayBodyOnCall (cuisineType: String, location:String, sortQuery: String){
        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).
        addConverterFactory(GsonConverterFactory.create()).
        build()
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("Bearer $API_KEY", cuisineType, location,limit,
            categoryAttribute,sortQuery).enqueue(object: Callback<YelpSearchResult>{
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG,"onResponse $response")
                val body = response.body()
                if (body==null){
                    Log.w(TAG,"Did not receive valid data from Yelp")
                    return
                }
                restaurants.addAll(body.businesses)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG,"onFailure $t")
            }

        })
    }
}