package com.app.sweetzy.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import com.app.sweetzy.R

object ProgressDialogCustom {

    private var dialog: Dialog? = null

    fun show(context: Context?) {
        if (isShowing()) {
            dialog = null
        }
        dialog = Dialog(context!!, R.style.Progress_Dialog_Horizontal)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setDimAmount(0.8f)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.setGravity(Gravity.CENTER)
        dialog!!.setContentView(R.layout.progress_custom)
        dialog!!.show()
    }

    fun hide() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun isShowing(): Boolean {
        return dialog != null && dialog!!.isShowing
    }
}