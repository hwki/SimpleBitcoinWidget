package com.brentpanther.bitcoinwidget.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.Coin

internal class CoinSelectionAdapter(private val listener: ((coin: Coin) -> Unit)?) : RecyclerView.Adapter<CoinSelectionAdapter.ViewHolder>() {

    private val coins: List<Coin> = Coin.values().sortedWith { o1, o2 -> String.CASE_INSENSITIVE_ORDER.compare(o1.coinName, o2.coinName)}

    internal class ViewHolder(v: CoinSelectionView) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CoinSelectionView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as CoinSelectionView).setCoin(coins[position], listener)
    }

    override fun getItemCount(): Int {
        return Coin.values().size
    }
}