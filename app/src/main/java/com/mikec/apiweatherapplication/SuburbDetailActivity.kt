package com.mikec.apiweatherapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mikec.apiweatherapplication.Models.Suburb

class SuburbDetailActivity:AppCompatActivity() {

    private lateinit var tvDetailLocation: TextView
    private lateinit var tvDetailWeather: TextView
    private lateinit var tvDetailTemperature: TextView
    private lateinit var tvLastUpdated: TextView
    private lateinit var tvDetailFeelsLike: TextView
    private lateinit var tvDetailHumidity: TextView
    private lateinit var tvDetailWind: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)
        setViews()
        init()
    }

    private fun setViews(){
        tvDetailLocation = findViewById(R.id.tvDetailLocation)
        tvDetailWeather= findViewById(R.id.tvDetailWeather)
        tvDetailTemperature = findViewById(R.id.tvDetailTemperature)
        tvDetailFeelsLike = findViewById(R.id.tvDetailFeelsLike)
        tvDetailHumidity = findViewById(R.id.tvDetailHumidity)
        tvDetailWind = findViewById(R.id.tvDetailWind)
        tvLastUpdated = findViewById(R.id.tvLastUpdated)
    }

    private fun init(){
        //Get Suburb from Intent
        val suburb = intent?.extras!!.getSerializable(getString(R.string.suburb)) as Suburb

        //Set Title and Back Button on Action Bar
        val actionbar = supportActionBar
        actionbar!!.title = suburb._name + " " + getString(R.string.details)
        actionbar.setDisplayHomeAsUpEnabled(true)

        //Set TextViews
        tvDetailLocation.text = suburb._name
        tvDetailWeather.text = suburb._weatherCondition
        tvDetailHumidity.text = suburb._weatherHumidity.substringAfter("Humidity: ")
        tvDetailWind.text = suburb._weatherWind.substringAfter("Wind: ")
        val temperature = suburb._weatherTemp + getString(R.string.celcius)
        tvDetailTemperature.text = temperature
        val feelslike = suburb._weatherFeelsLike + getString(R.string.celcius)
        tvDetailFeelsLike.text = feelslike
        val sdf = java.text.SimpleDateFormat("HH:mm:ss dd-MM-yyyy")
        val date = java.util.Date(suburb._weatherLastUpdated * 1000)
        val lastupdated = getString(R.string.lastupdated) + " " + sdf.format(date)
        tvLastUpdated.text = lastupdated
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}