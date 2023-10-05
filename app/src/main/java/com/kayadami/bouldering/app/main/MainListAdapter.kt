package com.kayadami.bouldering.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.app.main.type.MainItemType
import com.kayadami.bouldering.app.main.type.MainItemUiState
import com.kayadami.bouldering.databinding.BoulderingCellBinding
import com.kayadami.bouldering.databinding.EmptyCellBinding
import com.kayadami.bouldering.image.FragmentImageLoader
import com.kayadami.bouldering.image.ImageLoader
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class MainListAdapter @Inject constructor(
        @FragmentImageLoader val imageLoader: ImageLoader
) : RecyclerView.Adapter<MainListAdapter.MainViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, DIFF_CALLBACK)

    fun setList(newList: List<MainItemUiState>) {
        asyncListDiffer.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val ratio = when (MainItemType.values()[viewType]) {
            MainItemType.Portrait -> "3:4"
            MainItemType.Landscape -> "4:3"
            MainItemType.Square -> "1:1"
            else -> null
        }

        return when (ratio != null) {
            true -> BoulderingCellBinding.inflate(LayoutInflater.from(parent.context), parent, false).let {
                (it.imageThumbnail.layoutParams as? ConstraintLayout.LayoutParams)?.dimensionRatio = ratio

                BoulderingViewHolder(it)
            }
            false -> EmptyCellBinding.inflate(LayoutInflater.from(parent.context), parent, false).let {
                it.root.layoutParams = StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    isFullSpan = true
                }

                EmptyViewHolder(it)
            }
        }
    }

    override fun getItemViewType(position: Int) = asyncListDiffer.currentList[position].type.ordinal

    override fun getItemCount() = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(position)
    }

    sealed class MainViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(position : Int)

        abstract fun recycle()
    }

    inner class BoulderingViewHolder(
            val binding: BoulderingCellBinding
    ) : MainViewHolder(binding.root) {

        var data: MainItemUiState? = null

        init {
            binding.layoutContainer.setOnClickListener {
                data?.let {
                    it.onClick()
                }
            }
        }

        override fun bind(position: Int) {
            val item = asyncListDiffer.currentList[position]
            data = item

            imageLoader.load(binding.imageThumbnail, item.thumb, item.updatedAt)

            binding.textDate.text = item.displayDate

            binding.textSolved.visibility = when (item.isSolved) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        }

        override fun recycle() {
            data = null
        }
    }

    class EmptyViewHolder(
            binding: EmptyCellBinding
    ) : MainViewHolder(binding.root) {
        override fun bind(position: Int) {
            // DO NOTHING
        }

        override fun recycle() {
            // DO NOTHING
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MainItemUiState>() {
            override fun areItemsTheSame(oldItem: MainItemUiState, newItem: MainItemUiState): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MainItemUiState, newItem: MainItemUiState): Boolean {
                return oldItem.id == newItem.id && oldItem.updatedAt == newItem.updatedAt
            }
        }
    }
}