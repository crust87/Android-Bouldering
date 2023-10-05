package com.kayadami.bouldering.app.setting.opensourcelicense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crust87.bouldering.data.opensource.type.OpenSourceLicense
import com.kayadami.bouldering.databinding.OpenSourceLicenseCellBinding

class OpenSourceLicenseAdapter(
        val list: List<OpenSourceLicense>
) : RecyclerView.Adapter<OpenSourceLicenseAdapter.OpenSourceLicenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OpenSourceLicenseCellBinding.inflate(LayoutInflater.from(parent.context), parent, false).run {
        OpenSourceLicenseViewHolder(this)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: OpenSourceLicenseViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class OpenSourceLicenseViewHolder(val binding: OpenSourceLicenseCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.openSourceLicense = list[position]
        }
    }
}