package com.enact.asa.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.enact.asa.R
import com.enact.asa.utils.Constants
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import spencerstudios.com.bungeelib.Bungee

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    companion object {
        const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData == null) {
                    return@addOnSuccessListener
                }
                val deepLink: Uri? = pendingDynamicLinkData.link
                if (deepLink != null) {
                    //loading data from deep link in order to proceed requests
                    val asaConsumerCode = deepLink.getQueryParameter(Constants.ASA_CONSUMER_CODE) ?: ""
                    if (asaConsumerCode.isNotEmpty()) {
                        Log.d(TAG, "Consumer code loaded")
                        Paper.book()
                            .write(Constants.ASA_CONSUMER_CODE, asaConsumerCode)
                    }

                    val asaFintechCode = deepLink.getQueryParameter(Constants.ASA_FINTECH_CODE) ?: ""
                    if (asaFintechCode.isNotEmpty()) {
                        Log.d(TAG, "Fintech code loaded")
                        Paper.book()
                            .write(Constants.ASA_FINTECH_CODE, asaFintechCode)
                    }

                    val fintechName = deepLink.getQueryParameter(Constants.FINTECH_NAME) ?: ""
                    if (fintechName.isNotEmpty()) {
                        Log.d(TAG, "Fintech name loaded")
                        Paper.book()
                            .write(Constants.FINTECH_NAME, fintechName)
                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

        //starting next activity delayed by 3s
        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            Bungee.fade(context)
        }
    }
}