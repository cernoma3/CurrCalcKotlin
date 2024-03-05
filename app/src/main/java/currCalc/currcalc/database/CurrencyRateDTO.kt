package currCalc.currcalc.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import currCalc.currcalc.domain.CurrencyRate

@Entity(tableName = "currency_rates")
data class CurrencyRateDTO (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
)

fun CurrencyRateDTO.mapToDomain(): CurrencyRate {
    return CurrencyRate(
        baseCurrency = this.baseCurrency,
        targetCurrency = this.targetCurrency,
        rate = this.rate
    )
}