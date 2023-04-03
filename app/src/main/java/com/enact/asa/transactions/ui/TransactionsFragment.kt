package com.enact.asa.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.enact.asa.databinding.TransactionsFragmentBinding
import com.enact.asa.models.Transaction
import com.enact.asa.models.TransactionData

//showing user transactions
class TransactionsFragment : Fragment() {
    private var _binding: TransactionsFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var data: ArrayList<TransactionData>? = null
        set(value) {
            field = value
            setUi()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TransactionsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setUi() {
        if (_binding != null) {
            data?.let {
                val transactions = ArrayList<Transaction>()
                for (item in data!!) {
                    transactions.addAll(item.transactions)
                }
                binding.accountsRv.adapter = TransactionsAdapter(transactions)
                binding.accountsRv.layoutManager = LinearLayoutManager(context)
            }
        }
    }
}