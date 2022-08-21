package com.brentpanther.bitcoinwidget.ui.manage

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ValueWidgetProvider
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.databinding.FragmentManageWidgetsBinding
import com.brentpanther.bitcoinwidget.ui.BannerInflater
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
            val coinEntry = it.widget.toCoinEntry()
            intent.putExtra(SettingsActivity.EXTRA_COIN, coinEntry)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it.widget.widgetId)
            intent.putExtra(SettingsActivity.EXTRA_EDIT_WIDGET, true)
            startActivity(intent)
        }
        binding.listWidgets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.listWidgets.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                BannerInflater().inflate(layoutInflater, binding.layoutBanners)
                viewModel.getWidgets().distinctUntilChanged().collect {
                    binding.progress.isVisible = false
                    adapter.widgets = it
                    adapter.notifyDataSetChanged()
                    binding.empty.isVisible = it.isEmpty()
                    binding.listWidgets.isVisible = it.isNotEmpty()

                    // remove any orphaned widgets
                    val missingWidgets = it.map { w -> w.widget.widgetId }.minus(WidgetApplication.instance.widgetIds.toSet())
                    viewModel.deleteWidgets(missingWidgets.toIntArray())
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
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            binding.add.isVisible = true
            binding.add.setOnClickListener { toggleExpandedFab() }
            binding.addPrice.setOnClickListener { pinWidget(appWidgetManager, WidgetProvider::class.java) }
            binding.addValue.setOnClickListener { pinWidget(appWidgetManager, ValueWidgetProvider::class.java) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun <T : WidgetProvider> pinWidget(appWidgetManager: AppWidgetManager, className: Class<T>) {
        val myProvider = ComponentName(requireContext(), className)
        val intent = Intent(requireContext().applicationContext, CoinSelectionActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(requireContext().applicationContext, 123, intent, flags)
        appWidgetManager.requestPinAppWidget(myProvider, null, pendingIntent)
        toggleExpandedFab()
    }

    private fun toggleExpandedFab() {
        val isOpen = binding.addPrice.isVisible
        val fabAnimation = if (isOpen) R.anim.fab_close else R.anim.fab_open
        val fabTextAnimation = if (isOpen) R.anim.fab_text_close else R.anim.fab_text_open
        val fabSpinAnimation = if (isOpen) R.anim.fab_spin_off else R.anim.fab_spin_on
        binding.addPrice.isVisible = !isOpen
        binding.addValue.isVisible = !isOpen
        binding.textAddPrice.isVisible = !isOpen
        binding.textAddValue.isVisible = !isOpen
        binding.addPrice.startAnimation(AnimationUtils.loadAnimation(requireContext(), fabAnimation))
        binding.textAddPrice.startAnimation(AnimationUtils.loadAnimation(requireContext(), fabTextAnimation))
        binding.addValue.startAnimation(AnimationUtils.loadAnimation(requireContext(), fabAnimation))
        binding.textAddValue.startAnimation(AnimationUtils.loadAnimation(requireContext(), fabTextAnimation))
        binding.add.startAnimation(AnimationUtils.loadAnimation(requireContext(), fabSpinAnimation))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}