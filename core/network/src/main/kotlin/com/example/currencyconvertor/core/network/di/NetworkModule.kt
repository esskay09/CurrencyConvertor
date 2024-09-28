package com.example.currencyconvertor.core.network.di

import com.example.currencyconvertor.core.network.CurrencyNetworkDataSource
import com.example.currencyconvertor.core.network.ktor.KtorNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesCurrencyNetworkDataSource(json: Json): CurrencyNetworkDataSource = KtorNetwork(json)
}
