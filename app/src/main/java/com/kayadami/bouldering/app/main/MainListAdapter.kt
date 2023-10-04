package com.kayadami.bouldering.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.app.main.type.BoulderingListItem
import com.kayadami.bouldering.app.main.type.MainListItem
import com.kayadami.bouldering.constants.Orientation
import com.kayadami.bouldering.databinding.BoulderingCellBinding
import com.kayadami.bouldering.databinding.EmptyCellBinding
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.image.FragmentImageLoader
import com.kayadami.bouldering.image.ImageLoader
import com.kayadami.bouldering.list.ViewHolder
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class MainListAdapter @Inject constructor(
        @FragmentImageLoader val imageLoader: ImageLoader
) : RecyclerView.Adapter<ViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, DIFF_CALLBACK)

    private var _listener: BoulderingItemEvent? = null

    fun setList(newList: List<MainListItem>) {
        asyncListDiffer.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ratio = when (viewType) {
            Orientation.ORIENTATION_PORT -> "3:4"
            Orientation.ORIENTATION_LAND -> "4:3"
            Orientation.ORIENTATION_SQUARE -> "1:1"
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

    override fun getItemViewType(position: Int) = asyncListDiffer.currentList[position].type

    override fun getItemCount() = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class BoulderingViewHolder(
            val binding: BoulderingCellBinding
    ) : ViewHolder(binding.root) {

        var data: Bouldering? = null

        init {
            binding.layoutContainer.setOnClickListener {
                data?.let {
                    _listener?.onClick(it)
                }
            }
        }

        override fun bind(position: Int) {
            val itemData = asyncListDiffer.currentList[position].let {
                (it as BoulderingListItem).bouldering
            }.also {
                data = it
            }

            imageLoader.load(binding.imageThumbnail, itemData)

            binding.textDate.text = itemData.getDate()

            binding.textSolved.visibility = when (itemData.isSolved) {
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
    ) : ViewHolder(binding.root) {
        override fun bind(position: Int) {
            // DO NOTHING
        }

        override fun recycle() {
            // DO NOTHING
        }
    }

    fun setBoulderingItemEventListener(l: ((bouldering: Bouldering) -> Unit)?) {
        _listener = l?.let {
            object: BoulderingItemEvent {
                override fun onClick(bouldering: Bouldering) {
                    it.invoke(bouldering)
                }
            }
        }
    }

    interface BoulderingItemEvent {
        fun onClick(bouldering: Bouldering)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MainListItem>() {
            override fun areItemsTheSame(oldItem: MainListItem, newItem: MainListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MainListItem, newItem: MainListItem): Boolean {
                return oldItem.id == newItem.id && oldItem.updatedAt == newItem.updatedAt
            }
        }
    }
}