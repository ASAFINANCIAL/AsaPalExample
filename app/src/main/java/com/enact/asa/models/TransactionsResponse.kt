package com.enact.asa.models

data class TransactionsResponse(
    val data: List<TransactionData>,
    val message: String,
    val reference: List<Reference>,
    val status: Int,
    val version: String
)