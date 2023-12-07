package com.enact.asa.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.button.MaterialButton
import io.paperdb.Paper
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import spencerstudios.com.bungeelib.Bungee

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

        setupViewPager(tabViewpager)
        tabLayout.setupWithViewPager(tabViewpager, true)

        showProgressDialog()
        getUserInfo()
        getTransactionData()
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
        val asaConsumerCode = Paper.book().read(com.enact.asa.utils.Constants.asaConsumerCode, "")
        val asaFintechCode = Paper.book().read(com.enact.asa.utils.Constants.asaFintechCode, "")
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
        val asaConsumerCode = Paper.book().read(com.enact.asa.utils.Constants.asaConsumerCode, "")
        val asaFintechCode = Paper.book().read(com.enact.asa.utils.Constants.asaFintechCode, "")
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