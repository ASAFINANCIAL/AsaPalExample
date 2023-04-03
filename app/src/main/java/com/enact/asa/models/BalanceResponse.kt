package com.enact.asa.models

data class BalanceResponse(
    val data: List<BalanceItem>,
    val message: String,
    val reference: List<Reference>,
    val status: Int,
    val version: String
)