package com.example.rentalmanagement.utils

import android.content.Context
import java.text.NumberFormat
import java.util.Currency

class NumberUtils {
    companion object {
        fun convertCurrency(amount: Int): String {
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.setMaximumFractionDigits(0)
            format.currency = Currency.getInstance("VND")
            return format.format(amount)
        }

        fun dpToPx(dp: Int, context: Context): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density + 0.5f).toInt()
        }
    }
}