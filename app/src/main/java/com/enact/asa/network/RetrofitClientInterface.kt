package com.enact.asa.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientInterface {
    private const val BASE_URL = "https://openapi.asacore.com/"

    private var api: ApiInterface? = null
    fun getAPI(): ApiInterface? {
        if (api == null) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val logging = HttpLoggingInterceptor()
            // set your desired log level
            logging.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request: Request =
                            chain.request().newBuilder().addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .build()

                        return chain.proceed(request)
                    }
                }).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()


            api = retrofit.create(ApiInterface::class.java)

        }
        return api
    }
}