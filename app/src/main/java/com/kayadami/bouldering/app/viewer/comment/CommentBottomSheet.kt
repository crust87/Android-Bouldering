package com.kayadami.bouldering.app.viewer.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kayadami.bouldering.databinding.CommentBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentBottomSheet: BottomSheetDialogFragment() {

    lateinit var binding: CommentBottomSheetBinding

    val viewModel: CommentViewModel by viewModels()

    lateinit var adapter: CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = CommentBottomSheetBinding.inflate(inflater, container, false).apply {
            viewModel = this@CommentBottomSheet.viewModel
            lifecycleOwner = this@CommentBottomSheet
        }

        adapter = CommentAdapter()

        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boulderingId = arguments?.getLong(BOULDERING_ID) ?: 0

        viewModel.init(boulderingId)

        viewModel.eventChannel.onEach {
            when(it) {
                is OnNewCommentEvent -> {
                    binding.recyclerView.smoothScrollToPosition(0)
                    adapter.refresh()
                }
            }
        }.launchIn(lifecycleScope)

        lifecycleScope.launch {
            viewModel.getList().collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        lifecycleScope.coroutineContext.cancelChildren()
    }

    companion object {
        private const val BOULDERING_ID = "BOULDERING_ID_KEY"

        fun create(boulderingId: Long): CommentBottomSheet {
            return CommentBottomSheet().apply {
                arguments = bundleOf(
                    BOULDERING_ID to boulderingId
                )
            }
        }
    }
}