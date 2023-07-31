package com.kayadami.bouldering.app.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.constants.Orientation
import com.kayadami.bouldering.databinding.BoulderingCellBinding
import com.kayadami.bouldering.databinding.EmptyCellBinding
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.image.FragmentImageLoader
import com.kayadami.bouldering.image.ImageLoader
import com.kayadami.bouldering.list.ViewHolder
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
open class BoulderingAdapter @Inject constructor(
        @FragmentImageLoader val imageLoader: ImageLoader
) : RecyclerView.Adapter<ViewHolder>() {

    private var _listener: BoulderingItemEvent? = null

    val list = ArrayList<Bouldering>()

    fun setList(newList: List<Bouldering>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
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
                it.imageLoader = this@BoulderingAdapter.imageLoader

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

    override fun getItemViewType(position: Int) = when (list.isNotEmpty()) {
        true -> list[position].viewType
        false -> Orientation.NONE
    }

    override fun getItemCount() = when (list.isNotEmpty()) {
        true -> list.size
        false -> 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class BoulderingViewHolder(
            val binding: BoulderingCellBinding
    ) : ViewHolder(binding.root) {

        init {
            binding.setListener {
                binding.bouldering?.let {
                    _listener?.onClick(it)
                }
            }
        }

        override fun bind(position: Int) {
            binding.bouldering = list[position]
        }

        override fun recycle() {
            binding.bouldering = null
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
        _listener = if (l == null) {
            null
        } else {
            object: BoulderingItemEvent {
                override fun onClick(bouldering: Bouldering) {
                    l.invoke(bouldering)
                }
            }
        }
    }

    interface BoulderingItemEvent {
        fun onClick(bouldering: Bouldering)
    }
}