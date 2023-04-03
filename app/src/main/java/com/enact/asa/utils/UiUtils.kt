package com.enact.asa.utils

import java.text.DecimalFormat

fun Double.convertToDollar(): String {
    val format = DecimalFormat("#,###.00")
    return "$${format.format(this)}"
}