package com.enact.asa.models

data class Account(
    val account_id: String,
    val balances: Balances,
    val mask: Any,
    val name: String,
    val official_name: Any,
    val subtype: Any,
    val type: Any,
    val verification_status: String
)