package com.enact.asa.models

data class TransactionData(
    val account: Account,
    val request_id: String,
    val total_transactions: Int,
    val transactions: List<Transaction>
)