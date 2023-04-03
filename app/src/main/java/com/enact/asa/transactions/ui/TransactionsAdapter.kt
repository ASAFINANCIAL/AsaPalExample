package com.enact.asa.transactions.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enact.asa.R
import com.enact.asa.models.Transaction
import com.enact.asa.utils.convertToDollar
import com.google.android.material.textview.MaterialTextView


class TransactionsAdapter(private val list: ArrayList<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        var transactionText: MaterialTextView
        var balanceText: MaterialTextView
        var transactionDate: MaterialTextView

        init {
            transactionText = view.findViewById(R.id.transaction_text)
            balanceText = view.findViewById(R.id.balance)
            transactionDate = view.findViewById(R.id.transaction_date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.transaction_item,
                parent,
                false
            )
        return TransactionHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val data = list[position]
        holder.transactionText.text = data.name
        holder.transactionDate.text = data.date
        try {
            holder.balanceText.text = data.amount.convertToDollar()
        } catch (e: Exception) {
            holder.balanceText.text = "$${data.amount}"
        }
    }
}