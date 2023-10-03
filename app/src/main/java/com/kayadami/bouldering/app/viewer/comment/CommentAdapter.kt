package com.kayadami.bouldering.app.viewer.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kayadami.bouldering.data.comment.type.Comment
import com.kayadami.bouldering.databinding.CommentBottomSheetCellBinding
import com.kayadami.bouldering.utils.DateUtils

class CommentAdapter: PagingDataAdapter<Comment, CommentAdapter.CommentViewHolder>(DIFF_CALLBACK) {

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
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)
        }
    }

    class CommentViewHolder(val binding: CommentBottomSheetCellBinding): ViewHolder(binding.root) {

        var data: Comment? = null

        fun bind(item: Comment) {
            data = item

            binding.textComment.text = item.text
            binding.textCreatedAt.text = DateUtils.convertDate(item.createdAt)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.id == newItem.id && oldItem.text == newItem.text
        }
    }
}