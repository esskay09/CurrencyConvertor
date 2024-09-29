package com.example.currencyconvertor.core.data.model;

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
}