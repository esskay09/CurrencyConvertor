package com.example.currencyconvertor.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
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
    suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>)

    @Transaction
    @Query("SELECT * FROM exchange_rate WHERE base_currency_id = :baseId")
    fun getExchangeRates(baseId: String): Flow<List<ExchangeRateEntity>>

    @Query(
        """
        DELETE FROM exchange_rate
        WHERE base_currency_id = :currencyId OR target_currency_id = :currencyId
    """
    )
    suspend fun deleteExchangeRates(currencyId: String)
}
