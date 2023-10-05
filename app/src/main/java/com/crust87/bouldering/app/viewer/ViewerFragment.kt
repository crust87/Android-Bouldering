package com.crust87.bouldering.app.viewer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.navArgs
import com.crust87.bouldering.R
import com.crust87.bouldering.app.viewer.domain.HideKeyboardUseCase
import com.crust87.bouldering.app.viewer.domain.OpenKeyboardUseCase
import com.crust87.bouldering.app.navigate
import com.crust87.bouldering.app.navigateUp
import com.crust87.bouldering.app.viewer.comment.CommentBottomSheet
import com.crust87.bouldering.data.asEditorBouldering
import com.crust87.bouldering.databinding.ViewerFragmentBinding
import com.crust87.bouldering.editor.EditorView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ViewerFragment : Fragment() {

    @Inject
    lateinit var openKeyboardUseCase: OpenKeyboardUseCase

    @Inject
    lateinit var hideKeyboardUseCase: HideKeyboardUseCase

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
                    viewModel.finishEditTitle(it.text.toString())
                    true
                } else {
                    false
                }
            }

            it.setListener { _, _ ->
                viewModel.finishEditTitle(it.text.toString())
            }
        }

        viewModel.init(args.boulderingId)

        viewModel.uiState.map { it.data }.distinctUntilChanged().observe(viewLifecycleOwner) {
            it?.let {
                binding.editorView.setProblem(it.asEditorBouldering())
            }
        }

        binding.editorView.setOnProblemListener(object: EditorView.OnProblemListener{
            override fun onLoadingStart() {
                viewModel.setLoading(true)
            }

            override fun onLoadingFinish() {
                viewModel.setLoading(false)
            }
        })

        viewModel.uiState.map { it.isKeyboardOpen }.distinctUntilChanged().observe(viewLifecycleOwner) {
            if (it) {
                openKeyboardUseCase(binding.textTitle)
            } else {
                hideKeyboardUseCase(binding.textTitle)
            }
        }

        viewModel.uiState.map { it.message }.distinctUntilChanged().observe(viewLifecycleOwner) {
            val newMessage = it ?: ""

            if (newMessage.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

                viewModel.consumeMessage()
            }
        }

        viewModel.uiState.map { it.isRemoved }.distinctUntilChanged().observe(viewLifecycleOwner) {
            if (it) {
                navigateUp()
            }
        }

        viewModel.eventChannel.onEach {
            when (it) {
                is OpenEditorEvent -> navigate(
                    ViewerFragmentDirections.actionViewerFragmentToEditorFragment(it.id)
                )
                is OpenCommentEvent -> openComment()
                is OpenShareEvent -> startActivity(it.intent)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun openComment() {
        CommentBottomSheet.create(
            args.boulderingId
        ).show(childFragmentManager, "CommentBottomSheet")
    }
}