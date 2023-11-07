package com.example.android_challenge.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import java.text.NumberFormat
import java.util.Currency

fun View.showToast(context: Context, message: String){
    Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
}

