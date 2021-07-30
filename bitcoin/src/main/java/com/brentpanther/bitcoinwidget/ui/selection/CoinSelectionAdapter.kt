package com.brentpanther.bitcoinwidget.ui.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.R

internal class CoinSelectionAdapter(private val onClickListener: ((coin: CoinEntry) -> Unit)) :
    RecyclerView.Adapter<CoinSelectionAdapter.MyViewHolder>(), Filterable {

    var coins: List<CoinEntry> = listOf()
        set(value) {
            field = value
            filteredCoins = value
        }
    var filteredCoins: List<CoinEntry> = listOf()
    private val myFilter = MyFilter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_coin_selector, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(filteredCoins[position], onClickListener)
    }

    override fun getItemCount() = filteredCoins.size

    internal class MyViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        val icon: ImageView = view.findViewById(R.id.coin_icon)
        val name: TextView = view.findViewById(R.id.coin_name)
        val symbol: TextView = view.findViewById(R.id.coin_symbol)

        fun bind(entry: CoinEntry, onClickListener: ((coin: CoinEntry) -> Unit)) {
            icon.setImageResource(entry.coin.icon)
            name.text = entry.name
            symbol.text = entry.symbol
            view.setOnClickListener { onClickListener.invoke(entry) }
        }
    }

    override fun getFilter() = myFilter

    @Suppress("UNCHECKED_CAST")
    inner class MyFilter : Filter() {

        override fun performFiltering(value: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = when {
                value.isNullOrBlank() -> coins
                else -> coins.filter { it.symbol.startsWith(value, true) || it.name.startsWith(value, true) }
            }
            return results
        }

        override fun publishResults(value: CharSequence, results: FilterResults) {
            filteredCoins = results.values as List<CoinEntry>
            notifyDataSetChanged()
        }

    }
}