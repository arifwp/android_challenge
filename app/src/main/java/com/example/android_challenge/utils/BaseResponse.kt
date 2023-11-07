package com.example.android_challenge.utils

sealed class BaseResponse<out T> {

    class Loading<out T> : BaseResponse<T>()
    data class Success<out T>(val data: T) : BaseResponse<T>()
    data class Error(val msg: String?) : BaseResponse<Nothing>()

}