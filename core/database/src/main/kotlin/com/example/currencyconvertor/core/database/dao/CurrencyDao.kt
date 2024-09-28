package com.example.currencyconvertor.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.currencyconvertor.core.database.model.ConversionRateEntity
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import com.example.currencyconvertor.core.database.model.CurrencyWithConversionRates
import kotlinx.coroutines.flow.Flow


@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Insert
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("DELETE FROM currency")
    suspend fun deleteCurrencies()

    @Transaction
    suspend fun updateCurrencies(currencies: List<CurrencyEntity>) {
        deleteCurrencies()
        insertCurrencies(currencies)
    }

    @Insert
    suspend fun insertConversionRates(rates: List<ConversionRateEntity>)

    @Transaction
    @Query("SELECT * FROM currency WHERE id = :baseId")
    fun getConversionRates(baseId: String): Flow<CurrencyWithConversionRates?>

    @Query("""
        DELETE FROM conversion_rate
        WHERE base_currency_id = :currencyId OR target_id = :currencyId
    """)
    suspend fun deleteConversationRates(currencyId: String)
}
