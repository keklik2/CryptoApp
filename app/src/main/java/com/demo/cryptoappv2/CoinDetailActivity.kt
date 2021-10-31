package com.demo.cryptoappv2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_coin_detail.*

class CoinDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)

        if (!intent.hasExtra(FROM_SYMBOL_EXTRA_KEY)) {
            finish()
            return
        }
        val fromSymbol = intent.getStringExtra(FROM_SYMBOL_EXTRA_KEY)

        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        fromSymbol?.let { it1 ->
            viewModel.getDetailInfo(it1).observe(this, {
                val shortNameTemplate = this.resources.getString(R.string.short_names)
                val priceTemplate = this.resources.getString(R.string.price)
                with(it) {
                    tvDetailTitle.text = String.format(shortNameTemplate, fromSymbol, toSymbol)
                    tvDetailPrice.text = String.format(priceTemplate, price)
                    tvDetailMin.text = String.format(priceTemplate, lowDay)
                    tvDetailMax.text = String.format(priceTemplate, highDay)
                    tvDetailLast.text = lastMarket
                    tvDetailUpdate.text = getFormattedTime()

                    Picasso.get().load(getFullImgUrl()).into(ivDetailLogo)
                }
            })
        }
    }

    companion object {
        private const val FROM_SYMBOL_EXTRA_KEY = "fSym"

        fun newIntent(context: Context, fromSymbol: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(FROM_SYMBOL_EXTRA_KEY, fromSymbol)
            return intent
        }
    }
}