package com.enact.asa.user_info.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.enact.asa.databinding.UserInfoFragmentBinding
import com.enact.asa.models.BalanceItem
import com.enact.asa.utils.BEConstants
import io.paperdb.Paper

//Shows basic user info received from deep link and information about accounts
class UserInfoFragment : Fragment() {
    private var _binding: UserInfoFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var data: ArrayList<BalanceItem>? = null
        set(value) {
            field = value
            setUi()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserInfoFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val asaConsumerCode = Paper.book().read(com.enact.asa.utils.Constants.asaConsumerCode, "")
        val asaFintechCode = Paper.book().read(com.enact.asa.utils.Constants.asaFintechCode, "")
        val fintechName = Paper.book().read(com.enact.asa.utils.Constants.FintechName, "")
        if (asaConsumerCode.isNullOrEmpty()) {
            binding.consumerId.text = BEConstants.CONSUMER_CODE
        } else {
            binding.consumerId.text = asaConsumerCode
        }
        if (asaFintechCode.isNullOrEmpty()) {
            binding.fintechId.text = BEConstants.FINTECH_CODE
        } else {
            binding.fintechId.text = asaFintechCode
        }
        if (fintechName.isNullOrEmpty()) {
            binding.fintechName.text = "ASA PAL"
        } else {
            binding.fintechName.text = fintechName
        }
    }

    private fun setUi() {
        if (_binding != null) {
            data?.let {
                binding.accountsRv.adapter = UserInfoAdapter(data!!)
                binding.accountsRv.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}