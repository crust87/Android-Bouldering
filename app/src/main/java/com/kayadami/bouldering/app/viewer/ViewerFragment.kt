package com.kayadami.bouldering.app.viewer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.domain.KeyboardHideUseCase
import com.kayadami.bouldering.app.domain.KeyboardOpenUseCase
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.viewer.comment.CommentBottomSheet
import com.kayadami.bouldering.databinding.ViewerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ViewerFragment : Fragment() {

    @Inject
    lateinit var keyboardOpenUseCase: KeyboardOpenUseCase

    @Inject
    lateinit var keyboardHideUseCase: KeyboardHideUseCase

    lateinit var binding: ViewerFragmentBinding

    val viewModel: ViewerViewModel by viewModels()

    val args: ViewerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewerFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@ViewerFragment.viewModel
            lifecycleOwner = this@ViewerFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editorView.setIsViewer(true)

        with(binding.toolbar) {
            title = ""
            inflateMenu(R.menu.menu_viewer)
            setNavigationIcon(R.drawable.ic_back)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionComment -> {
                        viewModel.openComment()

                        true
                    }

                    R.id.actionShare -> {
                        viewModel.openShare()

                        true
                    }

                    R.id.actionSaveAsImage -> {
                        viewModel.saveImage()

                        true
                    }

                    R.id.actionModify -> {
                        viewModel.openEditor()

                        true
                    }

                    R.id.actionDelete -> {
                        viewModel.remove(requireActivity())

                        true
                    }

                    else -> false
                }
            }

            setNavigationOnClickListener {
                navigateUp()
            }
        }

        binding.textTitle.also {
            it.setOnEditorActionListener { _, id, e ->
                if (id == EditorInfo.IME_ACTION_SEARCH || id == EditorInfo.IME_ACTION_DONE ||
                    e.action == KeyEvent.ACTION_DOWN && e.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    viewModel.finishEditTitle(it)
                    true
                } else {
                    false
                }
            }

            it.setListener { _, _ ->
                viewModel.finishEditTitle(it)
            }
        }

        viewModel.init(args.boulderingId)

        viewModel.eventChannel.onEach {
            when (it) {
                is OpenEditorEvent -> navigate(
                    ViewerFragmentDirections.actionViewerFragmentToEditorFragment(it.data.id)
                )
                is OpenCommentEvent -> openComment()
                is OpenShareEvent -> startActivity(it.intent)
                is FinishSaveEvent -> Toast.makeText(context, it.path, Toast.LENGTH_SHORT).show()
                is NavigateUpEvent -> navigateUp()
                is OpenKeyboardEvent -> keyboardOpenUseCase(it.editText)
                is HideKeyboardEvent -> keyboardHideUseCase(it.editText)
                is ToastEvent -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }.launchIn(lifecycleScope)
    }

    fun openComment() {
        CommentBottomSheet.create(
            args.boulderingId
        ).show(childFragmentManager, "CommentBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        lifecycleScope.coroutineContext.cancelChildren()
    }
}