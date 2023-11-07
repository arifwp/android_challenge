package com.example.android_challenge.ui.home

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android_challenge.databinding.FragmentTransactionBinding
import com.example.android_challenge.models.History
import com.example.android_challenge.utils.BaseResponse
import com.example.android_challenge.utils.Helper
import com.example.android_challenge.utils.SharedPref
import com.example.android_challenge.utils.showToast
import com.example.android_challenge.viewmodel.TransactionViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private val args: TransactionFragmentArgs by navArgs()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var uid: String? = null
    private var productPrice: Int = 0
    private var transactionID: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productPrice = args.productPrice!!.toInt()
        uid = SharedPref.getUid(requireContext())
        transactionViewModel.updateUserBalance(uid.toString(), productPrice, 0)
        observe()

        transactionID = Random().nextInt(100000 - 1000) + 2
        with(binding){
            idTrx.setText("ID${transactionID.toString()}")
            price.setText(Helper.currencyFormatter(productPrice).toString())
            merchantName.setText(args.productName.toString())
            btnProceed.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        generateQrCode("BNI.ID${transactionID}.${args.productName}.${args.productPrice}")
    }

    private fun observe() {
        transactionViewModel.updateUserBalanceResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    binding.root.showToast(requireContext(), "Anda akan diarahkan kembali ke halaman home setelah 5 detik")
                    val history = History(
                        transactionID = "ID${transactionID}",
                        productID = args.productId,
                        uid = uid,
                        productName = args.productName,
                        productPhoto = args.productPhoto,
                        productPrice = args.productPrice!!.toInt()
                    )
                    transactionViewModel.saveTransaction(history)
                }
                is BaseResponse.Error -> {
                    binding.root.showToast(requireContext(), it.msg.toString())
                }
            }
        }

        transactionViewModel.saveTransactionResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    lifecycleScope.launch {
                        backToHome()
                    }
                }
                is BaseResponse.Error -> {
                    binding.root.showToast(requireContext(), it.msg.toString())
                }
            }
        }
    }

    private suspend fun backToHome() {
        delay(10000)
        findNavController().navigate(TransactionFragmentDirections.actionTransactionFragmentToNavigationHome())
    }

    fun generateQrCode(content: String) {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for(x in 0 until width){
            for(y in 0 until height){
                bmp.setPixel(x, y, if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
            }
        }
        binding.img.setImageBitmap(bmp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}