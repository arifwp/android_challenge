package com.example.android_challenge.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android_challenge.R
import com.example.android_challenge.databinding.FragmentResultScannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultScannerFragment : Fragment() {

    private var _binding: FragmentResultScannerBinding? = null
    private val binding get() = _binding!!
    private val args: ResultScannerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        with(binding){
            tvResult.setText(args.result)
            btnBack.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.result_scanner_fragment){
                    findNavController().navigate(ResultScannerFragmentDirections.actionResultScannerFragmentToNavigationHome())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}