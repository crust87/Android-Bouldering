package com.kayadami.bouldering.app.viewer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.databinding.ViewerFragmentBinding
import com.kayadami.bouldering.utils.PermissionChecker
import com.kayadami.bouldering.utils.PermissionChecker2.Companion.isAllGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.viewer_fragment.*

@AndroidEntryPoint
class ViewerFragment : Fragment() {

    private lateinit var fragmentBinding: ViewerFragmentBinding
    private val viewModel: ViewerViewModel by viewModels()

    val args: ViewerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = ViewerFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@ViewerFragment.viewModel
            lifecycleOwner = this@ViewerFragment
        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(fragmentBinding.toolbar) {
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
                        if (PermissionChecker.check(requireContext())) {
                            viewModel.saveImage()
                        } else {
                            PermissionChecker.request(this@ViewerFragment, PermissionChecker.REQUEST_SAVE)
                        }

                        true
                    }
                    else -> false
                }
            }

            setNavigationOnClickListener {
                navigateUp()
            }
        }

        with(fragmentBinding.editorView) {
            setOnClickListener {
                if (textTitle.isFocusableInTouchMode) {
                    hideKeyboard()
                    textTitle.isFocusable = false
                    viewModel.setTitle(textTitle.text.toString())
                } else {
                    viewModel.isShow.set(!viewModel.isShow.get())
                }
            }

            setIsViewer(true)
        }

        with(fragmentBinding.textTitle) {
            setOnClickListener {
                if (!isFocusableInTouchMode) {
                    isFocusableInTouchMode = true
                    if (requestFocus()) {
                        openKeyboard(this)
                    }
                }
            }

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

        viewModel.requestSavePermissionEvent.observe(viewLifecycleOwner) {
            savePermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        viewModel.start(args.boulderingId)
    }

    val savePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            viewModel.saveImage()
        } else {
            viewModel.toastEvent.value = "NO PERMISSIONS!"
        }
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
                    navigateUp()
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