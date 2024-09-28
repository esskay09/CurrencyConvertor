package com.example.currencyconvertor.core.network.ktor

import com.example.currencyconvertor.core.network.CurrencyNetworkDataSource
import com.example.currencyconvertor.core.network.model.ExchangeRatesNetwork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


private const val BASE_URL = "https://openexchangerates.org/api"

// Note: In a production environment, this app ID should not be hardcoded.
// It is recommended to securely fetch sensitive data like API keys from a backend service or a secure storage solution.
// For the purposes of this assignment and to maintain smooth functionality, this constant is used directly here.
private const val APP_ID = "edd316e0ca7749f0b844e566eea53822"

internal class KtorNetwork(json: Json) : CurrencyNetworkDataSource {

    private val networkApi = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(DefaultRequest) {
            header(HttpHeaders.Authorization, "Token $APP_ID")
        }
    }

    override suspend fun getCurrencies(): Map<String, String> {
        return networkApi.get("$BASE_URL/currencies.json").body()
    }

    override suspend fun getExchangeRates(base: String): ExchangeRatesNetwork {
        return networkApi.get("$BASE_URL/latest.json").body()
    }

}
