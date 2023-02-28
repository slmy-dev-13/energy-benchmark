package com.slmy.form_pwa

fun formatDecimal(double: Double, digits: Int = 2): dynamic {
    return double.asDynamic().toFixed(digits)
}