package com.enact.asa.models

data class Transaction(
    val account_id: String,
    val account_owner: String,
    val amount: Double,
    val authorized_date: String,
    val authorized_datetime: String,
    val category: String,
    val category_id: Any,
    val check_number: Any,
    val date: String,
    val datetime: String,
    val iso_currency_code: String,
    val location: Any,
    val merchant_name: String,
    val name: String,
    val payment_channel: Any,
    val payment_meta: Any,
    val pending: Boolean,
    val pending_transaction_id: Any,
    val personal_finance_category: Any,
    val transaction_code: String,
    val transaction_id: String,
    val transaction_type: String,
    val unofficail_currency_code: String
)