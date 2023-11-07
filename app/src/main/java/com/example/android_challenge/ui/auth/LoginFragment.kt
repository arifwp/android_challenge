package com.example.android_challenge.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.android_challenge.R
import com.example.android_challenge.databinding.FragmentLoginBinding
import com.example.android_challenge.utils.BaseResponse
import com.example.android_challenge.utils.SharedPref
import com.example.android_challenge.utils.showToast
import com.example.android_challenge.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN: Int = 1
    lateinit var gso: GoogleSignInOptions
    lateinit var mAuth: FirebaseAuth
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG = "LoginFragment"
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            doCheck()
        }

        mAuth = FirebaseAuth.getInstance()
        createRequest()

        val btnLogin = binding.btnLogin
        btnLogin.setOnClickListener {
            validateLogin()
        }
    }

    private fun doCheck() {
        val uid = SharedPref.getUid(requireContext())

        if (!uid.isNullOrEmpty()){
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNavigationHome())
        }
    }

    private fun validateLogin() {
        val loginIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(loginIntent, RC_SIGN_IN)
        authViewModel.loginResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let{
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNavigationHome())
                    }
                    is BaseResponse.Error -> {
                        binding.root.showToast(requireContext(), it.msg.toString())
                    }
                }
            }
        }
    }

    private fun createRequest() {
        // Configure Google Sign In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            SharedPref.saveUID(requireContext(), mAuth.currentUser?.uid.toString())
                            authViewModel.login(
                                mAuth.currentUser!!.uid,
                                mAuth.currentUser!!.displayName.toString(),
                                mAuth.currentUser!!.email.toString(),
                                mAuth.currentUser!!.photoUrl.toString(),

                            )
                        } else {
                            binding.root.showToast(requireContext(), task.exception?.message.toString())
                        }
                    }
            } catch (e: ApiException) {
                binding.root.showToast(requireContext(), e.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}