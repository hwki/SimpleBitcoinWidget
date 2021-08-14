package com.brentpanther.bitcoinwidget.ui.manage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.databinding.ActivityManageWidgetsBinding

class ManageWidgetsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityManageWidgetsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageWidgetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.navigation.setOnItemSelectedListener {
            val fragment = when(it.itemId) {
                R.id.action_list_widgets -> ManageWidgetsFragment()
                R.id.action_settings -> ManageSettingsFragment()
                else -> throw IllegalArgumentException()
            }
            supportFragmentManager.commit { replace(R.id.fragment_container_view, fragment) }
            true
        }
        binding.navigation.setOnItemReselectedListener {  }
    }
}