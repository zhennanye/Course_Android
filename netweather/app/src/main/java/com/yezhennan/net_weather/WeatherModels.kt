package com.yezhennan.net_weather

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("city_code") val cityCode: String?,
    @SerializedName("city_name") val cityName: String?
)

data class WeatherResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: WeatherData?
)

data class WeatherData(
    @SerializedName("shidu") val shidu: String?,
    @SerializedName("pm25") val pm25: Double?,
    @SerializedName("pm10") val pm10: Double?,
    @SerializedName("quality") val quality: String?,
    @SerializedName("wendu") val wendu: String?,
    @SerializedName("ganmao") val ganmao: String?,
    @SerializedName("forecast") val forecast: List<WeatherForecast>?
)

data class WeatherForecast(
    @SerializedName("ymd") val ymd: String?,
    @SerializedName("week") val week: String?,
    @SerializedName("high") val high: String?,
    @SerializedName("low") val low: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("fx") val fx: String?,
    @SerializedName("fl") val fl: String?,
    @SerializedName("notice") val notice: String?
)
