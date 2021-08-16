package com.brentpanther.bitcoinwidget.ui.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.WidgetViews
import com.brentpanther.bitcoinwidget.databinding.ListItemManageWidgetBinding
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import com.brentpanther.bitcoinwidget.ui.preview.LocalWidgetPreview

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
        return widgets[position].widget.theme.layout
    }

    override fun getItemCount() = widgets.count()

    class MyViewHolder(private val binding: ListItemManageWidgetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(widgetSettings: WidgetSettings, onClickListener: (settings: WidgetSettings) -> Unit) {
            with(binding) {
                val preview = LocalWidgetPreview(widgetPreview)
                val widgetViews = WidgetViews(root.context, preview, widgetSettings)
                with(widgetSettings.widget) {
                    widgetViews.setText(lastValue, true)
                    // need to find at runtime since view hierarchy updated after binding
                    binding.root.findViewById<View>(R.id.parent).isClickable = false
                    val coinName = coinUnit ?: currencySymbol ?: coin.name
                    binding.labelCoin.text = root.context.getString(R.string.widget_list_title, coinName, currency)
                    binding.labelExchange.text = widgetSettings.widget.exchange.exchangeName
                    root.setOnClickListener { onClickListener(widgetSettings) }
                }

            }
         
        }

    }
}