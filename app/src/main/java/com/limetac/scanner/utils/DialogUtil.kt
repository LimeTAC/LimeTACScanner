package com.limetac.scanner.utils

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.children
import com.limetac.scanner.R

object DialogUtil {

    fun showTagDialog(context: Context, msg: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_tag_detail)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val text = dialog.findViewById(R.id.dialogTagDetail_title) as TextView
        text.text = msg
        val okButton: Button = dialog.findViewById(R.id.dialogTagDetail_okBtn) as Button
        val copyButton: Button = dialog.findViewById(R.id.dialogTagDetail_copyCodeBtn) as Button
        okButton.setOnClickListener {
            dialog.dismiss()
        }
        copyButton.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", msg)
            clipboard.setPrimaryClip(clip)
            ToastUtil.createShortToast(context, "Code has been copied!")
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showEnvironmentChangeDialog(context: Context, preference: Preference) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_select_environment)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val radioGroup = dialog.findViewById(R.id.environmentRadioGroup) as RadioGroup
        /**
         * Set Previous Selected Environment
         */
        for (radioBtn in radioGroup.children) {
            radioBtn as RadioButton
            if (radioBtn.getTag() == preference.getStringPrefrence(
                    Constants.PreferenceKeys.CURRENT_ENVIRONMENT_KEY,
                    ""
                )
            ) {
                radioBtn.isChecked = true
            }
        }

        radioGroup.setOnCheckedChangeListener { group, _ ->
            val selectedId = radioGroup.checkedRadioButtonId
            val radio: RadioButton = group.findViewById(selectedId)
            Log.e("selectedtext-->", radio.text.toString())
            when (selectedId) {
                R.id.release_radio -> {
                    Constants.Environment.CURRENT_ENVIRONMENT = "https://rctgsmobile.limetac.com"
                    preference.saveStringInPrefrence(
                        Constants.PreferenceKeys.CURRENT_ENVIRONMENT_KEY,
                        Constants.Environment.CURRENT_ENVIRONMENT
                    )
                }
                R.id.dev_radio -> {
                    Constants.Environment.CURRENT_ENVIRONMENT = "https://devtgs.limetac.com"
                    preference.saveStringInPrefrence(
                        Constants.PreferenceKeys.CURRENT_ENVIRONMENT_KEY,
                        Constants.Environment.CURRENT_ENVIRONMENT
                    )
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}