package com.kayadami.bouldering.app.viewer

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.databinding.ViewerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewerFragment : Fragment() {

    private lateinit var binding: ViewerFragmentBinding
    private val viewModel: ViewerViewModel by viewModels()

    val args: ViewerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
                    R.id.actionModify -> {
                        viewModel.openEditor()

                        true
                    }
                    R.id.actionDelete -> {
                        deleteBouldering()

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
                    else -> false
                }
            }

            setNavigationOnClickListener {
                navigateUp()
            }
        }

        with(binding.textTitle) {
            setOnEditorActionListener { _, id, e ->
                if (id == EditorInfo.IME_ACTION_SEARCH || id == EditorInfo.IME_ACTION_DONE ||
                        e.action == KeyEvent.ACTION_DOWN && e.keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    isFocusable = false
                    viewModel.setTitle(text.toString())
                    true
                } else {
                    false
                }
            }

            setListener { _, _ ->
                isFocusable = false
                viewModel.setTitle(text.toString())
            }
        }

        viewModel.openEditorEvent.observe(viewLifecycleOwner) {
            openEditor(it)
        }

        viewModel.openShareEvent.observe(viewLifecycleOwner) {
            startActivity(it)
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.finishSaveEvent.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateUpEvent.observe(viewLifecycleOwner) {
            navigateUp()
        }

        viewModel.openKeyboardEvent.observe(viewLifecycleOwner) {
            openKeyboard(it)
        }

        viewModel.hideKeyboardEvent.observe(viewLifecycleOwner) {
            hideKeyboard()
        }

        viewModel.start(args.boulderingId)
    }

    private fun openKeyboard(v: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus
        view?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun deleteBouldering() {
        val activity = activity ?: return

        AlertDialog.Builder(activity)
                .setMessage(R.string.alert_delete)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.remove()
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
    }

    private fun openEditor(id: Long) {
        ViewerFragmentDirections.actionViewerFragmentToEditorFragment().apply {
            boulderingId = id
        }.also {
            navigate(it)
        }
    }
}