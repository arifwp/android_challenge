package com.example.android_challenge.utils

import android.content.Context
import com.example.android_challenge.utils.SharedPrefManager.getEncryptedSpf

object SharedPref {

    const val UID ="uid"

    fun saveUID(context: Context, uid: String){
        savePrefsUid(context, UID, uid)
    }

    fun getUid(context: Context): String? {
        return getStringUid(context, UID)
    }

    fun getStringUid(context: Context, key: String): String? {
        val prefs = getEncryptedSpf(context)
        return prefs?.getString(this.UID, null)
    }

    fun savePrefsUid(context: Context, key: String, value: String){
        var prefs = getEncryptedSpf(context)
        var editor = prefs?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun clearData(context: Context){
        var prefsRole = getEncryptedSpf(context)
        var editorRole = prefsRole?.edit()
        editorRole?.clear()
        editorRole?.apply()
    }

}