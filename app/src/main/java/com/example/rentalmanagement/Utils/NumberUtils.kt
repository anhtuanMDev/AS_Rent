package com.example.rentalmanagement.Utils

import java.text.NumberFormat
import java.util.Currency

class NumberUtils {
    fun convertCurrency(amount: Int):String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.currency = Currency.getInstance("VND")
        return format.format(amount)
    }
}