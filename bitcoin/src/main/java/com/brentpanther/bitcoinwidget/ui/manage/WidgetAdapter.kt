package com.brentpanther.bitcoinwidget.ui.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.databinding.ListItemManageWidgetBinding
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import com.brentpanther.bitcoinwidget.strategy.display.WidgetDisplayStrategy
import com.brentpanther.bitcoinwidget.strategy.presenter.PreviewWidgetPresenter

class WidgetAdapter(private val onClickListener: ((settings: WidgetSettings) -> Unit)) :
    RecyclerView.Adapter<WidgetAdapter.MyViewHolder>() {

    var widgets = listOf<WidgetSettings>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListItemManageWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.widgetPreview.widgetContainer.removeAllViews()
        View.inflate(parent.context, viewType, binding.widgetPreview.widgetContainer)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(widgets[position], onClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return widgets[position].widget.theme.lightPrice
    }

    override fun getItemCount() = widgets.count()

    class MyViewHolder(private val binding: ListItemManageWidgetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(widgetSettings: WidgetSettings, onClickListener: (settings: WidgetSettings) -> Unit) {
            with(binding) {
                val widget = widgetSettings.widget
                val widgetPresenter = PreviewWidgetPresenter(widget, this.widgetPreview)
                val strategy = WidgetDisplayStrategy.getStrategy(binding.root.context, widget, widgetPresenter)
                strategy.refresh()

                // need to find at runtime since view hierarchy updated after binding
                binding.root.findViewById<View>(R.id.parent).isClickable = false
                val coinName = widget.coinUnit ?: widget.coinCustomName ?: widget.coin.name
                val price = binding.root.findViewById<TextView>(R.id.price)
                TextViewCompat.setAutoSizeTextTypeWithDefaults(price, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                binding.labelCoin.text = root.context.getString(R.string.widget_list_title, coinName, widget.currency)
                binding.labelExchange.text = widget.exchange.exchangeName
                root.setOnClickListener { onClickListener(widgetSettings) }
            }
         
        }

    }
}