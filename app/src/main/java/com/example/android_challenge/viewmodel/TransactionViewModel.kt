package com.example.android_challenge.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_challenge.models.History
import com.example.android_challenge.models.User
import com.example.android_challenge.utils.BaseResponse
import com.example.android_challenge.utils.SingleLiveEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val database: FirebaseDatabase
): ViewModel(), LifecycleObserver {


    private val TAG = "TransactionViewModel"
    private val _updateUserBalanceResponse = MutableLiveData<BaseResponse<String>>()
    val updateUserBalanceResponse: LiveData<BaseResponse<String>> = _updateUserBalanceResponse

    private val _saveTransactionResponse = MutableLiveData<BaseResponse<String>>()
    val saveTransactionResponse: LiveData<BaseResponse<String>> = _saveTransactionResponse

    fun saveTransaction(history: History){
        viewModelScope.launch {
            try {
                val dataHistory = History(
                    transactionID = history.transactionID,
                    productID = history.productID,
                    uid = history.uid,
                    productName = history.productName,
                    productPhoto = history.productPhoto,
                    productPrice = history.productPrice.toInt()
                )
                Log.d(TAG, "saveTransaction: $dataHistory")
                val ref = database.reference.child("history")
                ref.child(history.transactionID.toString()).setValue(dataHistory).addOnCompleteListener {
                    if(it.isSuccessful){
                        _saveTransactionResponse.postValue(BaseResponse.Success("Berhasil masuk"))
                    } else {
                        _saveTransactionResponse.postValue(BaseResponse.Error("Gagal masuk! Coba beberapa saat lagi"))
                    }
                }
            } catch (e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _saveTransactionResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun updateUserBalance(uid: String, price: Int, multiplyBalance: Int){
        viewModelScope.launch {
            try {
                val queryUser = database.reference.child("users")
                queryUser.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (childSnapshot in snapshot.children){
                                val key = childSnapshot.key
                                val balance = childSnapshot.child("balance").getValue(Int::class.java)
                                if(key != null){
                                    if (price == 0 && multiplyBalance != 0){
                                        queryUser.child(key).child("balance").setValue(multiplyBalance).addOnCompleteListener { multiply ->
                                            if (multiply.isSuccessful){
                                                _updateUserBalanceResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                                            } else {
                                                _updateUserBalanceResponse.postValue(BaseResponse.Error("Gagal mengubah data"))
                                            }
                                        }
                                    } else {
                                        val finalBalance = balance!!.toInt() - price
                                        queryUser.child(key).child("balance").setValue(finalBalance).addOnCompleteListener { task ->
                                            if(task.isSuccessful) {
                                                _updateUserBalanceResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                                            } else {
                                                _updateUserBalanceResponse.postValue(BaseResponse.Error("Gagal mengubah data"))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _updateUserBalanceResponse.postValue(BaseResponse.Error("Gagal mengubah data"))
                    }

                })
            } catch (e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _updateUserBalanceResponse.postValue(BaseResponse.Error(error[1]))
            }


        }
    }
}