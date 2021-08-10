package com.brentpanther.bitcoinwidget.ui.selection

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.CoinEntry
import com.brentpanther.bitcoinwidget.databinding.ViewCoinSelectorBinding

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
        val binding = ViewCoinSelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(filteredCoins[position], onClickListener)
    }

    override fun getItemCount() = filteredCoins.size

    internal class MyViewHolder(private val binding: ViewCoinSelectorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: CoinEntry, onClickListener: ((coin: CoinEntry) -> Unit)) {
            with(binding) {
                coinIcon.setImageResource(entry.coin.icon)
                coinName.text = entry.name
                coinSymbol.text = entry.symbol
                root.setOnClickListener { onClickListener(entry) }
            }
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

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(value: CharSequence, results: FilterResults) {
            filteredCoins = results.values as List<CoinEntry>
            notifyDataSetChanged()
        }

    }
}