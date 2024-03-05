package currCalc.currcalc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM currency_rates WHERE baseCurrency = :baseCurrency")
    fun getRatesForBaseCurrency(baseCurrency: String): Flow<List<CurrencyRateDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg rates: CurrencyRateDTO)

    @Query("DELETE FROM currency_rates")
    suspend fun deleteAllRates()
}