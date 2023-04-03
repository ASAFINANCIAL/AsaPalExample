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
                val keyList: ArrayList<String> = ArrayList()
                val nameValuePair: ArrayList<String> = ArrayList()
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData == null) {
                    return@addOnSuccessListener
                }
                val deepLink: Uri? = pendingDynamicLinkData.link
                if (deepLink != null) {
                    //loading data from deep link in order to proceed requests
                    val asaConsumerCode = deepLink.getQueryParameter("AsaConsumerCode") ?: ""
                    if (asaConsumerCode.isNotEmpty()) {
                        Log.d(TAG, "Consumer code loaded")
                        Paper.book()
                            .write(Constants.asaConsumerCode, asaConsumerCode)
                    }

                    nameValuePair.add("ASA Consumer ID : $asaConsumerCode")
                    keyList.add("AsaConsumerCode")

                    val asaFintechCode = deepLink.getQueryParameter("AsaFintechCode") ?: ""
                    if (asaFintechCode.isNotEmpty()){
                        Log.d(TAG, "Fintech code loaded")
                        Paper.book()
                            .write(Constants.asaFintechCode, asaFintechCode)
                    }

                    val fintechName = deepLink.getQueryParameter("FintechName") ?: ""
                    if (fintechName.isNotEmpty()){
                        Log.d(TAG, "Fintech name loaded")
                        Paper.book()
                            .write(Constants.FintechName, fintechName)
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