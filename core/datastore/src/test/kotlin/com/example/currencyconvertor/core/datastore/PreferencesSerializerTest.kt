package com.example.currencyconvertor.core.datastore

import androidx.datastore.core.CorruptionException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class PreferencesSerializerTest {
    private val preferencesSerializer = PreferencesSerializer()

    @Test
    fun defaultUserPreferences_isEmpty() {
        assertEquals(
            preferencesProtoModel {
                // Default value
            },
            preferencesSerializer.defaultValue,
        )
    }

    @Test
    fun writingAndReadingPreferences_outputsCorrectValue() = runTest {
        val expectedPreferences = preferencesProtoModel {
            networkFetchTimeStampsProtoModel {
                currenciesFetchTimeStamp = 123
                currencyRatesFetchTimeStamp = 456
            }
        }
        val outputStream = ByteArrayOutputStream()
        expectedPreferences.writeTo(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val actualUserPreferences = preferencesSerializer.readFrom(inputStream)

        assertEquals(
            expectedPreferences,
            actualUserPreferences,
        )
    }

    @Test(expected = CorruptionException::class)
    fun readingInvalidPreferences_throwsCorruptionException() = runTest {
        preferencesSerializer.readFrom(ByteArrayInputStream(byteArrayOf(0)))
    }
}
