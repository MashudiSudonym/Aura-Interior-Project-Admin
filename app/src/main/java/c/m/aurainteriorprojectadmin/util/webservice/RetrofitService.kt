package c.m.aurainteriorprojectadmin.util.webservice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    companion object {
        fun getInstance(baseUrl: String): Retrofit {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val httpClient = OkHttpClient.Builder().apply {
                addInterceptor(loggingInterceptor)
            }
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
    }
}