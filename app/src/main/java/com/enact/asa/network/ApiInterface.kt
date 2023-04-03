package com.enact.asa.network

import com.enact.asa.models.BalanceResponse
import com.enact.asa.models.TransactionsResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

//this is the the open API available to anyone outside the system
interface ApiInterface {
    //suppressing needed because the hashmap is having Any as second parameter
    @GET("Balance/Accounts")
    @JvmSuppressWildcards
    suspend fun getBalanceAccounts(@HeaderMap headers: Map<String, Any>): Response<BalanceResponse?>

    @POST("Transactions")
    @JvmSuppressWildcards
    suspend fun getTransactions(@HeaderMap headers: Map<String, Any>?, @Body params: RequestBody?): Response<TransactionsResponse?>
}