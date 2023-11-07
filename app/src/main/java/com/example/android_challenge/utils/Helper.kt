package com.example.android_challenge.utils

import android.view.View
import java.text.NumberFormat
import java.util.Currency

object Helper {
    fun currencyFormatter(currency: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("IDR")
        return format.format(currency)
    }
}