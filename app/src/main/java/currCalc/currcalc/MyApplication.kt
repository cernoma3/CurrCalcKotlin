package currCalc.currcalc

import android.app.Application
import androidx.room.Room
import currCalc.currcalc.api.ExchangeRateApiService
import currCalc.currcalc.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class MyApplication : Application(), CoroutineScope {

    private val applicationJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = applicationJob

    lateinit var currencyRepository: CurrencyRepository
        private set

    override fun onCreate() {
        super.onCreate()
        initializeRepository()
        refreshCurrencyRates("USD")
        refreshCurrencyRates("EUR")
        refreshCurrencyRates("JPY")
        refreshCurrencyRates("GBP")
        refreshCurrencyRates("CAD")
        refreshCurrencyRates("CZK")
        refreshCurrencyRates("RUB")
        refreshCurrencyRates("HUF")

    }

    private fun initializeRepository() {
        val appDatabase = AppDatabase.getDatabase(this)
        currencyRepository = CurrencyRepository(exchangeRateApiService, appDatabase)
    }

    private fun refreshCurrencyRates(baseCurrency: String) {
        launch {
            currencyRepository.refreshRates(baseCurrency)
        }
    }

    private val exchangeRateApiService: ExchangeRateApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ExchangeRateApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApiService::class.java)
    }
}