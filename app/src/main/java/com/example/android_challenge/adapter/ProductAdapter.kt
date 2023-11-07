package com.example.android_challenge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_challenge.btnCheckoutListener
import com.example.android_challenge.databinding.LayoutProductBinding
import com.example.android_challenge.models.Product
import com.example.android_challenge.utils.Helper
import com.squareup.picasso.Picasso
import java.util.Random

class ProductAdapter(private var product: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: LayoutProductBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductAdapter.ProductViewHolder {
        val binding = LayoutProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        val data = product[position]
        val randomPrice = Random().nextInt(100000 - 1000) + 1
        with(holder.binding){
            tvPrice.text = Helper.currencyFormatter(data.productPrice).toString()
            tvProductName.text = data.productName
            Picasso.get()
                .load(data.productPhoto)
                .into(productImg)
            btnCheckout.setOnClickListener {
                listenerEditProject?.btnOnCheckout(data)
            }
        }
    }

    var listenerEditProject: btnCheckoutListener? = null

    override fun getItemCount(): Int = product.size

    fun setProductAdapter(productData: List<Product>){
        this.product = productData
        notifyDataSetChanged()
    }


}