package currCalc.currcalc.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import currCalc.currcalc.CurrencyRepository
import currCalc.currcalc.database.CurrencyRateDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CurrencyViewModel(private val repository: CurrencyRepository) : ViewModel() {

    private val _conversionResult = MutableStateFlow<Double?>(null)
    val conversionResult: StateFlow<Double?> = _conversionResult

    private val _exchangeRates = MutableStateFlow<List<CurrencyRateDTO>>(emptyList())
    val exchangeRates: StateFlow<List<CurrencyRateDTO>> = _exchangeRates.asStateFlow()

    fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String) {
        Log.d("CalculatorVM", "Converting $amount from $fromCurrency to $toCurrency")
        viewModelScope.launch {
            val rates = repository.getRatesForBaseCurrency(fromCurrency).firstOrNull()
            if (rates == null || rates.isEmpty()) {
                Log.d("CalculatorVM", "No rates available for $fromCurrency")
                _conversionResult.value = null
                return@launch
            }

            val rate = rates.find { it.targetCurrency == toCurrency }?.rate
            if (rate == null) {
                Log.d("CalculatorVM", "No rate found for $toCurrency in rates for $fromCurrency")
                _conversionResult.value = null
                return@launch
            }

            val convertedAmount = amount * rate
            Log.d("CalculatorVM", "Converted: $amount $fromCurrency to $convertedAmount $toCurrency at rate $rate")
            _conversionResult.value = convertedAmount
        }
    }

    fun loadExchangeRates(baseCurrency: String) {
        viewModelScope.launch {
            repository.getRatesForBaseCurrency(baseCurrency).firstOrNull()?.let { rates ->
                _exchangeRates.value = rates
            } ?: run {
                Log.d("CurrencyVM", "No exchange rates available for $baseCurrency")
                _exchangeRates.value = emptyList()
            }
        }
    }
}