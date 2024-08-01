package com.enact.asa.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.enact.asa.R
import com.enact.asa.models.BalanceResponse
import com.enact.asa.models.TransactionsResponse
import com.enact.asa.network.RetrofitClientInterface
import com.enact.asa.transactions.ui.TransactionsFragment
import com.enact.asa.user_info.ui.UserInfoFragment
import com.enact.asa.utils.BEConstants
import com.enact.asa.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.iosParameters
import io.paperdb.Paper
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import spencerstudios.com.bungeelib.Bungee
import kotlin.coroutines.resume

class MainActivity : BaseActivity() {

    private var userInfoFragment = UserInfoFragment()
    private var transactionsFragment = TransactionsFragment()

    private var transactionsJob: Job? = null
    private var userInfoJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val tabViewpager = findViewById<ViewPager>(R.id.dataViewager)
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_layout)
        val refreshBtn = findViewById<AppCompatTextView>(R.id.refreshBtn)

        refreshBtn.setOnClickListener {
            showProgressDialog()
            if (tabViewpager.currentItem == 1) {
                getTransactionData()
            } else {
                getUserInfo()
            }
        }

        findViewById<MaterialButton>(R.id.bano_btn).setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
            Bungee.slideLeft(this)
        }

        findViewById<AppCompatTextView>(R.id.billingBtn).setOnClickListener {
            showProgressDialog()
            redirectToAsaVault()
        }

        setupViewPager(tabViewpager)
        tabLayout.setupWithViewPager(tabViewpager, true)

        getUserInfo()
        getTransactionData()
        showProgressDialog()
    }

    private fun redirectToAsaVault() {
        lifecycleScope.launch {
            val dynamicLink = getStandardDynamicLink()
            if (dynamicLink.isEmpty()) {
                Toast.makeText(this@MainActivity, "Failed to create dynamic link", Toast.LENGTH_SHORT)
                    .show()
                hideProgress()
                return@launch
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dynamicLink))
            startActivity(intent)
            hideProgress()
        }
    }

    private suspend fun getStandardDynamicLink(): String =
        suspendCancellableCoroutine { continuation ->
            val androidId = "com.asa.vault.qa"
            val asaConsumerCode =
                Paper.book().read(Constants.ASA_CONSUMER_CODE, "")
            val asaFintechCode =
                Paper.book().read(Constants.ASA_FINTECH_CODE, "")
            val dynamicLinks = FirebaseDynamicLinks.getInstance()
            val shortLinkTask = dynamicLinks.createDynamicLink()
                .setLink(Uri.parse("https://www.asavault.com/?request=FintechBillingPage&asaConsumerCode=$asaConsumerCode&asaFintechCode=$asaFintechCode"))
                .setDomainUriPrefix("https://asavault.page.link") // replace with your domain prefix
            shortLinkTask.iosParameters("com.asa.vault.qa") {}
            shortLinkTask.androidParameters(androidId) {}
            shortLinkTask.buildShortDynamicLink()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val shortLink = task.result.shortLink
                        continuation.resume(shortLink.toString())
                    } else {
                        continuation.resume("")
                    }
                }
        }

    // This function is used to add items in arraylist and assign
    // the adapter to view pager
    private fun setupViewPager(viewpager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(userInfoFragment, "PersonalInfo")
        adapter.addFragment(transactionsFragment, "BankDetails")
        // setting adapter to view pager.
        viewpager.adapter = adapter
    }

    // This "ViewPagerAdapter" class overrides functions which are
    // necessary to get information about which item is selected
    // by user, what is title for selected item and so on.*/
    @Suppress("DEPRECATION")
    class ViewPagerAdapter// this is a secondary constructor of ViewPagerAdapter class.
        (supportFragmentManager: FragmentManager) :
        FragmentPagerAdapter(supportFragmentManager) {

        // objects of arraylist. One is of Fragment type and
        // another one is of String type.*/
        private var fragmentList1: ArrayList<Fragment> = ArrayList()
        private var fragmentTitleList1: ArrayList<String> = ArrayList()

        // returns which item is selected from arraylist of fragments.
        override fun getItem(position: Int): Fragment {
            return fragmentList1[position]
        }

        // returns which item is selected from arraylist of titles.
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1[position]
        }

        override fun getCount(): Int {
            return fragmentList1.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)
        }
    }

    private fun getTransactionData() {
        transactionsJob?.cancel()
        transactionsJob = null
        val asaConsumerCode = Paper.book().read(com.enact.asa.utils.Constants.ASA_CONSUMER_CODE, "")
        val asaFintechCode = Paper.book().read(com.enact.asa.utils.Constants.ASA_FINTECH_CODE, "")
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["Content-Type"] = "application/json"
        hashMap["Ocp-Apim-Subscription-Key"] = BEConstants.API_KEY
        hashMap["ApplicationCode"] = BEConstants.APP_CODE
        hashMap["AuthorizationKey"] = BEConstants.AUTHORIZATION_KEY

        //the file BEConstants is not available in the repo
        // you will need to get it from the ASA for your app
        if (asaConsumerCode.isNullOrEmpty())
            hashMap["asaConsumerCode"] = BEConstants.CONSUMER_CODE
        else
            hashMap["asaConsumerCode"] = asaConsumerCode

        if (asaFintechCode.isNullOrEmpty())
            hashMap["ASAFintechCode"] = BEConstants.FINTECH_CODE
        else
            hashMap["ASAFintechCode"] = asaFintechCode

        val mediaType = "application/json".toMediaTypeOrNull()
        val bodyHashMap = JSONObject().toString().toRequestBody(mediaType)

        transactionsJob = lifecycleScope.launch {
            val response: Response<TransactionsResponse?>
            try {
                response = RetrofitClientInterface.getAPI()?.getTransactions(hashMap, bodyHashMap)
                    ?: return@launch
            } catch (e: Exception) {
                //all network errors will be caught here
                Log.e("TAG", e.toString())
                hideProgress()
                return@launch
            }
            hideProgress()
            if (!response.isSuccessful) {
                return@launch
            }

            if (response.code() != 200) {
                return@launch
            }

            if (response.body() == null) {
                return@launch
            }
            transactionsFragment.data = ArrayList(response.body()!!.data)
        }
    }

    private fun getUserInfo() {
        userInfoJob?.cancel()
        userInfoJob = null
        val asaConsumerCode = Paper.book().read(com.enact.asa.utils.Constants.ASA_CONSUMER_CODE, "")
        val asaFintechCode = Paper.book().read(com.enact.asa.utils.Constants.ASA_FINTECH_CODE, "")
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["Content-Type"] = "application/json"
        hashMap["Ocp-Apim-Subscription-Key"] = BEConstants.API_KEY
        hashMap["ApplicationCode"] = BEConstants.APP_CODE
        hashMap["AuthorizationKey"] = BEConstants.AUTHORIZATION_KEY

        //the file BEConstants is not available in the repo
        // you will need to get it from the ASA for your app
        if (asaConsumerCode.isNullOrEmpty())
            hashMap["asaConsumerCode"] = BEConstants.CONSUMER_CODE
        else
            hashMap["asaConsumerCode"] = asaConsumerCode

        if (asaFintechCode.isNullOrEmpty())
            hashMap["ASAFintechCode"] = BEConstants.FINTECH_CODE
        else
            hashMap["ASAFintechCode"] = asaFintechCode

        userInfoJob = lifecycleScope.launch {
            val response: Response<BalanceResponse?>
            try {
                response = RetrofitClientInterface.getAPI()?.getBalanceAccounts(hashMap)
                    ?: return@launch
            } catch (e: Exception) {
                //all network errors will be caught here
                Log.e("TAG", e.toString())
                hideProgress()
                return@launch
            }
            hideProgress()
            if (!response.isSuccessful) {
                return@launch
            }

            if (response.code() != 200) {
                return@launch
            }

            if (response.body() == null) {
                return@launch
            }
            userInfoFragment.data = ArrayList(response.body()!!.data)
        }
    }
}