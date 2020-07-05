package com.mikec.apiweatherapplication.Models

import com.mikec.apiweatherapplication.Models.Country
import java.io.Serializable

data class Suburb (
    var _venueID: String,
    var _name: String,
    var _country: Country,
    var _weatherCondition: String,
    var _weatherConditionIcon: String,
    var _weatherWind: String,
    var _weatherHumidity: String,
    var _weatherTemp: String,
    var _weatherFeelsLike: String,
    var _weatherLastUpdated: Long
):Serializable