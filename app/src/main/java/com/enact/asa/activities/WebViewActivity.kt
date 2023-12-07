package com.enact.asa.activities

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import com.enact.asa.R

class WebViewActivity : BaseActivity() {

    companion object {
        const val URL = "https://bannodev.asacore.com/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy_activity)

        findViewById<ImageView>(R.id.back_btn).setOnClickListener {
            finish()
        }

        findViewById<WebView>(R.id.web_view).loadUrl(URL)
    }
}