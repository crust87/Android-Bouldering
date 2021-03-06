package com.kayadami.bouldering.app.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.databinding.ViewerFragmentBinding
import com.kayadami.bouldering.editor.EditorView
import kotlinx.android.synthetic.main.viewer_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ViewerFragment : Fragment() {

    private lateinit var fragmentBinding: ViewerFragmentBinding
    private val viewModel: ViewerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        arguments?.let { arguments ->
            val id = ViewerFragmentArgs.fromBundle(arguments).boulderingId
            viewModel.start(id)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = ViewerFragmentBinding.inflate(inflater, container, false)
        fragmentBinding.viewModel = viewModel
        fragmentBinding.lifecycleOwner = this

        viewModel.openEditorEvent.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { id ->
                openEditor(id)
            }
        })

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(com.kayadami.bouldering.R.drawable.ic_back)
        }

        with(editorView) {
            setOnClickListener {
                if (textTitle.isFocusableInTouchMode) {
                    hideKeyboard()
                    textTitle.isFocusable = false
                    viewModel.setTitle(textTitle.text.toString())
                } else {
                    viewModel.isShow.set(!viewModel.isShow.get())
                }
            }

            onProblemListener = object : EditorView.OnProblemListener {
                override fun onLoadingStart() {
                    viewModel.isProgress.set(true)
                }

                override fun onLoadingFinish() {
                    viewModel.isProgress.set(false)
                }
            }

            setIsViewer(true)
        }

        with(textTitle) {
            setOnClickListener { v ->
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
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.menu_viewer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                navigateUp()

                return true
            }
            R.id.actionModify -> {
                viewModel.openEditor()

                return true
            }
            R.id.actionDelete -> {
                deleteBouldering()

                return true
            }
            R.id.actionShare -> {
                openShare()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

    private fun openShare() = CoroutineScope(Dispatchers.Default).launch {
        viewModel.isProgress.set(true)

        try {
            val uri = viewModel.createShareImage()

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/*"

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "")
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "")
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Share Image"))
        } catch (e: Throwable) {
            Toast.makeText(context, "FAIL TO SHARE", Toast.LENGTH_SHORT).show()
        }

        viewModel.isProgress.set(false)
    }
}