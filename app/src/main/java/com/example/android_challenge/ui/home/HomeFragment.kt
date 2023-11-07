package com.example.android_challenge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_challenge.R
import com.example.android_challenge.databinding.FragmentHomeBinding
import com.example.android_challenge.adapter.ProductAdapter
import com.example.android_challenge.btnCheckoutListener
import com.example.android_challenge.models.Product
import com.example.android_challenge.utils.BaseResponse
import com.example.android_challenge.utils.Helper
import com.example.android_challenge.utils.SharedPref
import com.example.android_challenge.utils.showToast
import com.example.android_challenge.viewmodel.AuthViewModel
import com.example.android_challenge.viewmodel.HomeViewModel
import com.example.android_challenge.viewmodel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), btnCheckoutListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var uid: String? = null
    var balance: Int = 0
    var count: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateRv()
        observe()
        listener()


        uid = SharedPref.getUid(requireContext())
        authViewModel.getUserData(uid.toString())
        homeViewModel.getProduct()

    }

    private fun listener() {
        binding.wrapTopUp.setOnClickListener {
            val multi = balance*count++
            transactionViewModel.updateUserBalance(uid.toString(), 0, multi)
            binding.userBalance.setText(Helper.currencyFormatter(multi).toString())
        }

        binding.wrapWithdraw.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToCameraFragment())
        }

        binding.icLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            SharedPref.clearData(requireContext())
            if(findNavController().currentDestination?.id == R.id.navigation_home){
                findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToLoginFragment())
            }
        }
    }

    private fun observe() {
        homeViewModel.getProductResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        productAdapter.setProductAdapter(it.data)
                    }
                    is BaseResponse.Error -> {
                        binding.root.showToast(requireContext(), it.msg.toString())
                    }
                }
            }
        }

        authViewModel.getUserDataResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        balance = it.data.balance
                        binding.tvUsername.setText(it.data.name.toString())
                        binding.userBalance.setText(Helper.currencyFormatter(balance).toString())
                        if(!it.data.avatar.isNullOrBlank()){
                            Picasso.get()
                                .load(it.data.avatar)
                                .into(binding.imgUser)
                        }
                    }
                    is BaseResponse.Error -> {
                        binding.root.showToast(requireContext(), it.msg.toString())
                    }
                }
            }
        }
    }

    private fun initiateRv() {
        val recylerViewCategoryLocal: RecyclerView = binding!!.rvProduct
        productAdapter = ProductAdapter(ArrayList())
        productAdapter.listenerEditProject = this
        recylerViewCategoryLocal.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
            adapter = productAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun btnOnCheckout(data: Product) {
        val price = data.productPrice
        if(balance <= price){
            binding.root.showToast(requireContext(), "Saldo tidak cukup")
        } else {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToTransactionFragment(
                data.productID.toString(),
                data.productName.toString(),
                data.productPrice.toString(),
                data.productPhoto.toString()
            ))
        }

    }




}
