package currCalc.currcalc.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {
    companion object {
        const val BASE_URL = "https://v6.exchangerate-api.com/v6/"
        private const val API_KEY = "6bcf017e115d5cdc0f8da578"
    }

    @GET("${API_KEY}/latest/{base_code}")
    suspend fun getLatestRates(
        @Path("base_code") baseCode: String
    ): Response<CurrCalcNetwork>

}