package com.example.android_challenge.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_challenge.models.History
import com.example.android_challenge.models.Product
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
class HistoryViewModel @Inject constructor(
    private val database: FirebaseDatabase
): ViewModel(), LifecycleObserver {

    private val TAG = "HistoryViewModel"

    private val _getHistoryResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<History>>>>()
    val getHistoryResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<History>>>> = _getHistoryResponse

    fun getHistory(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("history").orderByChild("uid").equalTo(uid)
                val historyList: MutableList<History> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        historyList.clear()
                        for (childSnapshot in snapshot.children){
                            val history: History? = childSnapshot.getValue(History::class.java)
                            if(history != null){
                                historyList.add(history)
                            }
                            _getHistoryResponse.postValue(SingleLiveEvent(BaseResponse.Success(historyList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getHistoryResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getHistoryResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

}