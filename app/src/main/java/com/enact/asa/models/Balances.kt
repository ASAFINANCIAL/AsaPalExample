package com.enact.asa.models

data class Balances(
    val available: Double,
    val current: Double,
    val iso_currency_code: String,
    val limit: Int,
    val unofficial_currency_code: String
)