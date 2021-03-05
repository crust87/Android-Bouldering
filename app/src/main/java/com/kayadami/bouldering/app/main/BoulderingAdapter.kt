package com.kayadami.bouldering.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.constants.Orientation
import com.kayadami.bouldering.databinding.BoulderingCellBinding
import com.kayadami.bouldering.databinding.EmptyCellBinding
import com.kayadami.bouldering.list.ViewHolder
import com.kayadami.bouldering.utils.ImageLoader

open class BoulderingAdapter(val viewModel: MainViewModel, val imageLoader: ImageLoader) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ratio = when (viewType) {
            Orientation.ORIENTATION_PORT -> "3:4"
            Orientation.ORIENTATION_LAND -> "4:3"
            Orientation.ORIENTATION_SQUARE -> "1:1"
            else -> null
        }

        return when (ratio != null) {
            true -> BoulderingCellBinding.inflate(LayoutInflater.from(parent.context), parent, false).run {
                (imageThumbnail.layoutParams as? ConstraintLayout.LayoutParams)?.let {
                    it.dimensionRatio = ratio
                }

                BoulderingViewHolder(this, viewModel, this@BoulderingAdapter.imageLoader)
            }
            false -> EmptyCellBinding.inflate(LayoutInflater.from(parent.context), parent, false).run {
                EmptyViewHolder(this)
            }
        }
    }

    override fun getItemViewType(position: Int) = when (viewModel.isNotEmpty()) {
        true -> viewModel[position].viewType
        false -> Orientation.NONE
    }

    override fun getItemCount() = when (viewModel.isNotEmpty()) {
        true -> viewModel.size
        false -> 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    class BoulderingViewHolder(private val binding: BoulderingCellBinding,
                               private val viewModel: MainViewModel,
                               imageLoader: ImageLoader) : ViewHolder(binding.root) {

        private var currentPosition = -1

        init {
            binding.imageLoader = imageLoader
            binding.setListener {
                viewModel.openBoulderingEvent.value = currentPosition
            }
        }

        override fun bind(position: Int) {
            currentPosition = position
            binding.bouldering = viewModel[position]
        }

        override fun recycle() {
            currentPosition = -1
        }
    }

    class EmptyViewHolder(binding: EmptyCellBinding) : ViewHolder(binding.root) {

        init {
            itemView.layoutParams = StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                isFullSpan = true
            }
        }

        override fun bind(position: Int) {
            // DO NOTHING
        }

        override fun recycle() {
            // DO NOTHING
        }
    }
}