package com.brentpanther.bitcoinwidget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

class SettingsDialogFragment : DialogFragment() {

    private lateinit var mListener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(code: Int)
        fun onDialogNegativeClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val settingsFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("settings")
        mListener = settingsFragment as NoticeDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireActivity())
                .setTitle(arguments!!.getInt("title"))
                .setMessage(arguments!!.getInt("message"))
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                    mListener.onDialogPositiveClick(arguments!!.getInt("code"))
                }
        if (arguments!!.getBoolean("show_settings")) {
            dialog.setNegativeButton(R.string.button_settings) { _: DialogInterface, _: Int ->
                mListener.onDialogNegativeClick()
            }
        }
        return dialog.create()
    }

    companion object {
        fun newInstance(@StringRes title: Int, @StringRes message: Int,
                        code: Int, showSettingsButton: Boolean = true): SettingsDialogFragment {
            val fragment = SettingsDialogFragment()
            val bundle = Bundle()
            bundle.putInt("title", title)
            bundle.putInt("message", message)
            bundle.putInt("code", code)
            bundle.putBoolean("show_settings", showSettingsButton)
            fragment.arguments = bundle
            return fragment
        }
    }
}