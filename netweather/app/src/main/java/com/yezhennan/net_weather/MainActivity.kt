package com.yezhennan.net_weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.yezhennan.net_weather.ui.theme.NetweatherTheme
import java.io.IOException
import java.util.concurrent.Executors

private const val WEATHER_URL = "http://t.weather.itboy.net/api/weather/city/"

class MainActivity : ComponentActivity() {
    private val gson = Gson()
    private val cityLoader = Executors.newSingleThreadExecutor()
    private lateinit var requestQueue: RequestQueue

    private var cities by mutableStateOf<List<City>>(emptyList())
    private var citiesLoading by mutableStateOf(true)
    private var cityLoadError by mutableStateOf<String?>(null)
    private var selectedCity by mutableStateOf<City?>(null)
    private var weatherState by mutableStateOf<WeatherUiState>(WeatherUiState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestQueue = Volley.newRequestQueue(applicationContext)
        loadCities()

        setContent {
            NetweatherTheme {
                BackHandler(enabled = selectedCity != null) {
                    selectedCity = null
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val city = selectedCity
                    if (city == null) {
                        CityListScreen(
                            cities = cities,
                            loading = citiesLoading,
                            error = cityLoadError,
                            onRetry = ::loadCities,
                            onCitySelected = { selected ->
                                selectedCity = selected
                                loadWeather(selected)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        WeatherScreen(
                            city = city,
                            state = weatherState,
                            onBack = { selectedCity = null },
                            onRetry = { loadWeather(city) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun loadCities() {
        citiesLoading = true
        cityLoadError = null
        cityLoader.execute {
            try {
                val json = resources.openRawResource(R.raw.citycode)
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
                val loadedCities = gson.fromJson(json, Array<City>::class.java)
                    .filter { !it.cityCode.isNullOrBlank() && !it.cityName.isNullOrBlank() }
                    .distinctBy { it.cityCode }
                    .sortedBy { it.cityName }
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        cities = loadedCities
                        citiesLoading = false
                        if (loadedCities.isEmpty()) {
                            cityLoadError = getString(R.string.city_list_empty_error)
                        }
                    }
                }
            } catch (exception: IOException) {
                showCityLoadError(
                    getString(
                        R.string.city_file_read_error,
                        exception.localizedMessage ?: getString(R.string.file_unavailable)
                    )
                )
            } catch (exception: JsonParseException) {
                showCityLoadError(
                    getString(
                        R.string.city_file_parse_error,
                        exception.localizedMessage ?: getString(R.string.invalid_json)
                    )
                )
            }
        }
    }

    private fun showCityLoadError(message: String) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                citiesLoading = false
                cityLoadError = message
            }
        }
    }

    private fun loadWeather(city: City) {
        val cityCode = city.cityCode ?: return
        requestQueue.cancelAll(this)
        weatherState = WeatherUiState.Loading
        val request = StringRequest(
            Request.Method.GET,
            WEATHER_URL + cityCode,
            { body ->
                try {
                    val response = gson.fromJson(body, WeatherResponse::class.java)
                    val data = response?.data
                    if (response?.status == 200 && data != null) {
                        weatherState = WeatherUiState.Loaded(data)
                    } else {
                        weatherState = WeatherUiState.Error(
                            response?.message?.takeIf { it.isNotBlank() }
                                ?: getString(R.string.weather_service_unavailable)
                        )
                    }
                } catch (exception: JsonParseException) {
                    weatherState = WeatherUiState.Error(
                        getString(
                            R.string.weather_parse_error,
                            exception.localizedMessage ?: getString(R.string.invalid_json)
                        )
                    )
                }
            },
            { error ->
                weatherState = WeatherUiState.Error(
                    error.localizedMessage ?: getString(R.string.weather_request_error)
                )
            }
        )
        request.tag = this
        requestQueue.add(request)
    }

    override fun onDestroy() {
        if (::requestQueue.isInitialized) {
            requestQueue.cancelAll(this)
        }
        cityLoader.shutdownNow()
        super.onDestroy()
    }
}

private sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Loaded(val data: WeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

@Composable
private fun CityListScreen(
    cities: List<City>,
    loading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by androidx.compose.runtime.remember { mutableStateOf("") }
    val visibleCities = cities.filter {
        it.cityName.orEmpty().contains(query.trim(), ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.weather_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(stringResource(R.string.search_city)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> CenterMessage(stringResource(R.string.loading_city_list), showProgress = true)
            error != null -> CenterMessage(error, action = onRetry)
            visibleCities.isEmpty() -> CenterMessage(stringResource(R.string.no_matching_cities))
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visibleCities, key = { "${it.cityCode}-${it.cityName}" }) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCitySelected(city) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = city.cityName.orEmpty(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = city.cityCode.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherScreen(
    city: City,
    state: WeatherUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
            Text(
                text = city.cityName.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        when (state) {
            WeatherUiState.Loading -> CenterMessage(stringResource(R.string.loading_weather), showProgress = true)
            is WeatherUiState.Error -> CenterMessage(state.message, action = onRetry)
            is WeatherUiState.Loaded -> WeatherDetails(state.data)
        }
    }
}

@Composable
private fun WeatherDetails(data: WeatherData) {
    val placeholder = stringResource(R.string.value_unavailable)
    val currentType = data.forecast?.firstOrNull()?.type
    val iconDescription = stringResource(R.string.weather_icon_description)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Image(
                    painter = painterResource(weatherIconFor(currentType)),
                    contentDescription = iconDescription,
                    modifier = Modifier.height(96.dp)
                )
                Text(
                    stringResource(R.string.temperature_value, data.wendu ?: placeholder),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(currentType ?: stringResource(R.string.weather_information))
                Text(stringResource(R.string.humidity_value, data.shidu ?: placeholder))
                Text(stringResource(R.string.air_quality_value, data.quality ?: placeholder))
                Text(
                    stringResource(
                        R.string.air_particles_value,
                        data.pm25?.toDisplayString() ?: placeholder,
                        data.pm10?.toDisplayString() ?: placeholder
                    )
                )
                data.ganmao?.takeIf { it.isNotBlank() }?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.life_advice, it))
                }
            }
        }

        Text(
            stringResource(R.string.future_weather),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        val forecasts = data.forecast.orEmpty()
        if (forecasts.isEmpty()) {
            Text(stringResource(R.string.no_forecast))
        } else {
            forecasts.forEach { forecast ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(
                                R.string.forecast_date_week,
                                forecast.ymd.orEmpty(),
                                forecast.week.orEmpty()
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            painter = painterResource(weatherIconFor(forecast.type)),
                            contentDescription = iconDescription,
                            modifier = Modifier.height(56.dp)
                        )
                        Text(forecast.type ?: stringResource(R.string.weather_unknown))
                        Text(
                            stringResource(
                                R.string.temperature_range,
                                forecast.low ?: placeholder,
                                forecast.high ?: placeholder
                            )
                        )
                        Text(
                            stringResource(
                                R.string.wind_value,
                                forecast.fx ?: placeholder,
                                forecast.fl.orEmpty()
                            )
                        )
                        forecast.notice?.takeIf { it.isNotBlank() }?.let { Text(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterMessage(
    message: String,
    showProgress: Boolean = false,
    action: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showProgress) CircularProgressIndicator()
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (action != null) {
            Button(onClick = action) { Text(stringResource(R.string.retry)) }
        }
    }
}

private fun weatherIconFor(type: String?): Int {
    val weatherType = type.orEmpty()
    return when {
        weatherType.contains("雷") || weatherType.contains("电") -> R.drawable.thunder
        weatherType.contains("雪") -> R.drawable.snow
        weatherType.contains("雨") -> R.drawable.rain
        weatherType.contains("阴") || weatherType.contains("霾") || weatherType.contains("雾") ->
            R.drawable.cloud
        weatherType.contains("多云") || weatherType.contains("转") -> R.drawable.mcloud
        weatherType.contains("晴") -> R.drawable.sun
        else -> R.drawable.cloud
    }
}

private fun Double.toDisplayString(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()
