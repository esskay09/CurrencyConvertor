package com.example.currencyconvertor.core.data.model;

import com.example.currencyconvertor.core.network.model.ExchangeRatesNetwork
import org.junit.Assert.assertEquals
import org.junit.Test;

class CurrencyEntityTest {

    @Test
    fun network_currency_can_be_mapped_to_currency_entity() {
        val networkModel = mapOf(
            "USD" to "United States Dollar",
            "EUR" to "Euro"
        )
        val entity = mapNetworkCurrenciesToEntity(networkModel)

        assertEquals("USD", entity[0].id)
        assertEquals("United States Dollar", entity[0].name)
        assertEquals("EUR", entity[1].id)
        assertEquals("Euro", entity[1].name)
    }

    @Test
    fun network_exchange_rate_can_be_mapped_to_currency_entity() {
        val networkModel = ExchangeRatesNetwork(
            timestamp = 0,
            baseCurrencyId = "USD",
            rates = mapOf(
                "USD" to 1.0,
                "EUR" to 0.8
            )
        )
        val entity = networkModel.asEntityList("USD")

        assertEquals("USD", entity[0].baseCurrencyId)
        assertEquals(1.0, entity[0].rate, 0.0)
        assertEquals("EUR", entity[1].targetCurrencyId)
        assertEquals(0.8, entity[1].rate, 0.0)
    }
}