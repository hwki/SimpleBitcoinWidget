package com.brentpanther.bitcoinwidget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class CoinSelectionView(context: Context) : LinearLayout(context) {

    private lateinit var coin: Coin
    private var listener: ((coin: Coin) -> kotlin.Unit)? = null

    init {
        View.inflate(context, R.layout.view_coin_selector, this)
        val height = resources.getDimension(R.dimen.coin_selection_height).toInt()
        val layoutParams = ViewGroup.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        val verticalMargin = resources.getDimension(R.dimen.vertical_margin).toInt()
        val horizontalMargin = resources.getDimension(R.dimen.horizontal_margin).toInt()
        layoutParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin)
        setLayoutParams(layoutParams)
        setOnClickListener { listener?.invoke(coin) }
        setBackgroundResource(R.drawable.bg_rounded)
    }

    fun setCoin(coin: Coin, listener: ((coin: Coin) -> kotlin.Unit)?) {
        this.coin = coin
        this.listener = listener
        val name = findViewById<TextView>(R.id.coin_name)
        name.text = coin.coinName
        val image = findViewById<ImageView>(R.id.coin_icon)
        image.setImageResource(coin.icon)
    }

}
