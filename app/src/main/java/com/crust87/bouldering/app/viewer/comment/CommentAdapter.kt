package com.crust87.bouldering.app.viewer.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.crust87.bouldering.app.viewer.comment.type.CommentItemUIState
import com.crust87.bouldering.databinding.CommentBottomSheetCellBinding

class CommentAdapter: PagingDataAdapter<CommentItemUIState, CommentAdapter.CommentViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            CommentBottomSheetCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class CommentViewHolder(val binding: CommentBottomSheetCellBinding): ViewHolder(binding.root) {

        var data: CommentItemUIState? = null

        fun bind(item: CommentItemUIState) {
            data = item

            binding.textComment.text = item.text
            binding.textCreatedAt.text = item.createdAt
        }

        fun delete() {
            data?.delete?.invoke()
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentItemUIState>() {
            override fun areItemsTheSame(oldItem: CommentItemUIState, newItem: CommentItemUIState): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CommentItemUIState, newItem: CommentItemUIState): Boolean =
                oldItem == newItem
        }
    }
}