package com.brentpanther.bitcoinwidget.ui.manage

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.databinding.FragmentManageWidgetsBinding
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.CoinEntry
import com.brentpanther.bitcoinwidget.ui.selection.CoinSelectionActivity
import com.brentpanther.bitcoinwidget.ui.settings.SettingsActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ManageWidgetsFragment : Fragment() {

    private val viewModel : ManageWidgetsViewModel by viewModels()
    private var _binding: FragmentManageWidgetsBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageWidgetsBinding.inflate(layoutInflater)

        val adapter = WidgetAdapter {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            val coinEntry = CoinEntry(it.widget.coin.name, it.widget.coin.coinName, it.widget.coin.name, it.widget.coin)
            intent.putExtra(SettingsActivity.EXTRA_COIN, coinEntry)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it.widget.widgetId)
            intent.putExtra(SettingsActivity.EXTRA_EDIT_WIDGET, true)
            startActivity(intent)
        }
        binding.listWidgets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.listWidgets.adapter = adapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getWidgets().distinctUntilChanged().collect {
                    adapter.widgets = it
                    adapter.notifyDataSetChanged()
                    binding.empty.isVisible = it.isEmpty()
                    binding.listWidgets.isVisible = it.isNotEmpty()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupPinButton()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPinButton() {
        val appWidgetManager = requireContext().getSystemService(AppWidgetManager::class.java)
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            binding.add.isVisible = true
            binding.add.setOnClickListener {
                val myProvider = ComponentName(requireContext(), WidgetProvider::class.java)
                val intent = Intent(requireContext().applicationContext, CoinSelectionActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(requireContext().applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                appWidgetManager.requestPinAppWidget(myProvider, null, pendingIntent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}