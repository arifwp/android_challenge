package com.example.android_challenge.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_challenge.adapter.HistoryAdapter
import com.example.android_challenge.adapter.ProductAdapter
import com.example.android_challenge.databinding.FragmentHistoryBinding
import com.example.android_challenge.utils.BaseResponse
import com.example.android_challenge.utils.SharedPref
import com.example.android_challenge.utils.showToast
import com.example.android_challenge.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter
    private val historyViewModel: HistoryViewModel by viewModels()
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateRv()
        observe()
        uid = SharedPref.getUid(requireContext())

        historyViewModel.getHistory(uid.toString())

    }

    private fun observe() {
        historyViewModel.getHistoryResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        historyAdapter.setHistoryAdapter(it.data)
                    }
                    is BaseResponse.Error -> {
                        binding.root.showToast(requireContext(), it.msg.toString())
                    }
                }
            }
        }
    }

    private fun initiateRv() {
        val recylerViewCategoryLocal: RecyclerView = binding!!.rvHistory
        historyAdapter = HistoryAdapter(ArrayList())
        recylerViewCategoryLocal.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
            adapter = historyAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}