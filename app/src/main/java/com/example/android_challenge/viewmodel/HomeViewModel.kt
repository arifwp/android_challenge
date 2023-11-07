package com.example.android_challenge.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class HomeViewModel @Inject constructor(
    private val database: FirebaseDatabase
): ViewModel(), LifecycleObserver {

    private val TAG = "HomeViewModel"

    private val _getProductResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<Product>>>>()
    val getProductResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<Product>>>> = _getProductResponse

    fun getProduct(){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("product")
                val dataProductList: MutableList<Product> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataProductList.clear()
                        for (childSnapshot in snapshot.children){
                            val product: Product? = childSnapshot.getValue(Product::class.java)
                            if(product != null){
                                dataProductList.add(product)
                            }
                            _getProductResponse.postValue(SingleLiveEvent(BaseResponse.Success(dataProductList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getProductResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getProductResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }


}