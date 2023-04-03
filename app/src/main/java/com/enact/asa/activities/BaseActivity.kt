package com.enact.asa.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.enact.asa.R
import io.github.inflationx.viewpump.ViewPumpContextWrapper

open class BaseActivity : AppCompatActivity() {
    lateinit var context: Context
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        supportActionBar?.hide()
    }

    open fun hideProgress() {
        try {
            if (dialog != null && dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        hideProgress()
        super.onDestroy()
    }
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { ViewPumpContextWrapper.wrap(it) })
    }

    @SuppressLint("InflateParams")
    open fun showProgressDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.custom_progress, null)
        dialog?.setContentView(inflate)
        dialog?.setCancelable(false)
        dialog?.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog?.show()
    }
}
