package currCalc.currcalc.domain

data class CurrencyRate(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double
)