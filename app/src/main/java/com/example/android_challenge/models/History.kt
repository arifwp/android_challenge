package com.example.android_challenge.models

data class History (
    var transactionID: String? = null,
    var productID: String? = null,
    var uid: String? = null,
    var productName: String? = null,
    var productPhoto: String? = null,
    var productPrice: Int = 0,
)