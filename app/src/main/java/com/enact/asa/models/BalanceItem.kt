package com.enact.asa.models

data class BalanceItem(
    val accountName: String?,
    val accountNumber: String?,
    val asaConsumerCode: Int,
    val asaFiAccountCode: Int,
    val asaFintechCode: Int,
    val balance: Double,
    val currencyCode: Any,
    val dateOpened: String,
    val description: String?
)