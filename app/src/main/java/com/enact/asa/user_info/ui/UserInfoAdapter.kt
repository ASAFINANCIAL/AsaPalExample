package com.enact.asa.user_info.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enact.asa.R
import com.enact.asa.models.BalanceItem
import com.enact.asa.utils.convertToDollar
import com.google.android.material.textview.MaterialTextView

class UserInfoAdapter(private val list: ArrayList<BalanceItem>) : RecyclerView.Adapter<UserInfoAdapter.BalanceHolder>() {

    class BalanceHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameText: MaterialTextView
        var balanceText: MaterialTextView

        init {
            nameText = view.findViewById(R.id.account_name)
            balanceText = view.findViewById(R.id.balance)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.account_item,
                parent,
                false
            )
        return BalanceHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BalanceHolder, position: Int) {
        val data = list[position]
        if (data.accountName != null) {
            holder.nameText.text = data.accountName
        } else if (data.description != null) {
            holder.nameText.text = data.description
        } else if (data.accountNumber != null) {
            holder.nameText.text = data.accountNumber
        } else {
            holder.nameText.text = "Account"
        }
        try {
            holder.balanceText.text = data.balance.convertToDollar()
        } catch (e: Exception) {
            holder.balanceText.text = "$${data.balance}"
        }
    }

}