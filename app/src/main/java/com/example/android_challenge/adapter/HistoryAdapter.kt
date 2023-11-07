package com.example.android_challenge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_challenge.databinding.LayoutProductBinding
import com.example.android_challenge.models.History
import com.example.android_challenge.utils.Helper
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Currency
import java.util.Random

class HistoryAdapter(private var data: List<History>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: LayoutProductBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.HistoryViewHolder {
        val binding = LayoutProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryViewHolder, position: Int) {
        val data = data[position]
        val randomPrice = Random().nextInt(100000 - 1000) + 1
        with(holder.binding){
            btnCheckout.visibility = View.GONE
            tvPrice.text = Helper.currencyFormatter(data.productPrice).toString()
            tvProductName.text = data.productName
            Picasso.get()
                .load(data.productPhoto)
                .into(productImg)
        }
    }

    override fun getItemCount(): Int = data.size

    fun setHistoryAdapter(productData: List<History>){
        this.data = productData
        notifyDataSetChanged()
    }


}