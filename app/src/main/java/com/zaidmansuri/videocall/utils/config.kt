package com.zaidmansuri.videocall.utils


import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zaidmansuri.videocall.R

object config {
    var dialog: AlertDialog? = null

    fun showDialog(context: Context) {
        dialog = MaterialAlertDialogBuilder(context).setView(R.layout.loading_layout)
            .setCancelable(false).create()

        dialog!!.show()
    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }
}