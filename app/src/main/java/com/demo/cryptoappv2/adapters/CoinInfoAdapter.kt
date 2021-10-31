package com.demo.cryptoappv2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.cryptoappv2.R
import com.demo.cryptoappv2.pojo.CoinPriceInfo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_note.view.*

class CoinInfoAdapter(private val context: Context): RecyclerView.Adapter<CoinInfoAdapter.CoinInfoViewHolder>() {

    var coinInfoList: List<CoinPriceInfo> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return CoinInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
        val coin = coinInfoList[position]
        with(holder) {
            with(coin) {
                val shortNameTemplate = context.resources.getString(R.string.short_names)
                val priceTemplate = context.resources.getString(R.string.price)
                val lastUpdateTemplate = context.resources.getString(R.string.last_update)
                tvCardSymbols.text = String.format(shortNameTemplate, fromSymbol, toSymbol)
                tvCardPrice.text = String.format(priceTemplate, price)
                tvCardUpdateDate.text = String.format(lastUpdateTemplate, getFormattedTime())

                Picasso.get().load(getFullImgUrl()).into(ivCardLogo)

                itemView.setOnClickListener {
                    onCoinClickListener?.onCoinClick(this)
                }
            }
        }
    }

    override fun getItemCount(): Int = coinInfoList.size

    inner class CoinInfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivCardLogo: ImageView = itemView.ivCardLogo
        val tvCardSymbols: TextView = itemView.tvCardSymbols
        val tvCardPrice: TextView = itemView.tvCardPrice
        val tvCardUpdateDate: TextView = itemView.tvCardUpdateDate
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinPriceInfo)
    }
}