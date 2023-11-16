package com.crust87.bouldering.app.setting.opensourcelicense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crust87.bouldering.data.opensource.type.OpenSourceLicense
import com.crust87.bouldering.databinding.OpenSourceLicenseCellBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class OpenSourceLicenseAdapter @Inject constructor() : RecyclerView.Adapter<OpenSourceLicenseAdapter.OpenSourceLicenseViewHolder>() {

    var list : List<OpenSourceLicense> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OpenSourceLicenseCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .run {
                OpenSourceLicenseViewHolder(this)
            }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: OpenSourceLicenseViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class OpenSourceLicenseViewHolder(val binding: OpenSourceLicenseCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.openSourceLicense = list[position]
        }
    }
}