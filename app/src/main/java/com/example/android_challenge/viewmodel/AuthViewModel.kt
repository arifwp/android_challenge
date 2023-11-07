package com.example.android_challenge.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_challenge.models.User
import com.example.android_challenge.utils.BaseResponse
import com.example.android_challenge.utils.SingleLiveEvent
import com.google.android.gms.auth.api.identity.SignInPassword
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "AuthViewModel"
    var authCurrentUser = auth.currentUser
    private lateinit var reference: DatabaseReference

    private val _loginResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val loginResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _loginResponse

    private val _getUserDataResponse = MutableLiveData<SingleLiveEvent<BaseResponse<User>>>()
    val getUserDataResponse: LiveData<SingleLiveEvent<BaseResponse<User>>> = _getUserDataResponse

    fun getUserData(uid: String) {
        viewModelScope.launch {
            try {
                val ref = database.reference.child("users").child(uid)
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").getValue(String::class.java).toString()
                        val email = snapshot.child("email").getValue(String::class.java).toString()
                        val avatar = snapshot.child("avatar").getValue(String::class.java).toString()
                        val balance = snapshot.child("balance").getValue(Int::class.java).toString()
                        val user = User(
                            uid = uid,
                            name = name,
                            email = email,
                            avatar = avatar,
                            balance = balance.toInt()
                        )
                        _getUserDataResponse.postValue(SingleLiveEvent(BaseResponse.Success(user)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getUserDataResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _getUserDataResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun login(
        uid: String,
        name: String,
        email: String,
        avatar: String,
    ){
        viewModelScope.launch {
            try {
                val ref = FirebaseDatabase.getInstance().getReference("users")
                val user = User(
                    uid = uid,
                    name = name,
                    email = email,
                    avatar = avatar,
                    balance = 10000
                )
                ref.child(uid).setValue(user).addOnCompleteListener {
                    if(it.isSuccessful){
                        _loginResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil masuk")))
                    } else {
                        _loginResponse.postValue(SingleLiveEvent(BaseResponse.Error("Gagal masuk! Coba beberapa saat lagi")))
                    }
                }
            } catch (e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _loginResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

}