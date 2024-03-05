package currCalc.currcalc

import android.util.Log
import currCalc.currcalc.api.ExchangeRateApiService
import currCalc.currcalc.api.mapToDatabase
import currCalc.currcalc.database.AppDatabase
import currCalc.currcalc.database.CurrencyRateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CurrencyRepository(
    private val apiService: ExchangeRateApiService,
    private val database: AppDatabase
) {

    suspend fun refreshRates(baseCurrency: String) {
        Log.d("CurrencyRepository", "Refreshing rates for $baseCurrency")
        try {
            val response = apiService.getLatestRates(baseCurrency)
            if (response.isSuccessful) {
                database.currencyRateDao().deleteAllRates()
                Log.d("CurrencyRepository", "API Response: ${response.body()}")
                response.body()?.let { currCalcNetwork ->
                    val currencyRatesDTOs = currCalcNetwork.mapToDatabase()
                    withContext(Dispatchers.IO) {
                        currencyRatesDTOs.forEach { currencyRateDTO ->
                            database.currencyRateDao().insertAll(currencyRateDTO)
                        }
                    }
                    Log.d("CurrencyRepository", "Inserting ${currencyRatesDTOs.size} rates into the database for $baseCurrency")
                }
            } else {
                Log.e("CurrencyRepository", "Error fetching rates: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("CurrencyRepository", "Error fetching rates", e)
        }
    }

    fun getRatesForBaseCurrency(baseCurrency: String): Flow<List<CurrencyRateDTO>> {
        Log.d("CurrencyRepository", "Getting rates from DB for $baseCurrency")
        return database.currencyRateDao().getRatesForBaseCurrency(baseCurrency)
    }
}