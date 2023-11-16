package com.crust87.bouldering.app.setting.opensourcelicense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crust87.bouldering.data.opensource.type.OpenSourceLicense
import com.crust87.bouldering.databinding.OpenSourceLicenseCellBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class OpenSourceLicenseAdapter @Inject constructor() :
    PagingDataAdapter<OpenSourceLicense, OpenSourceLicenseAdapter.OpenSourceLicenseViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OpenSourceLicenseCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .run {
                OpenSourceLicenseViewHolder(this)
            }

    override fun onBindViewHolder(holder: OpenSourceLicenseViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class OpenSourceLicenseViewHolder(val binding: OpenSourceLicenseCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(openSourceLicense: OpenSourceLicense) {
            binding.openSourceLicense = openSourceLicense
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OpenSourceLicense>() {
            override fun areItemsTheSame(oldItem: OpenSourceLicense, newItem: OpenSourceLicense): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: OpenSourceLicense, newItem: OpenSourceLicense): Boolean =
                oldItem == newItem
        }
    }
}