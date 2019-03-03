package com.kayadami.bouldering.app.editor

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.kayadami.bouldering.Event
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.databinding.EditorFragmentBinding
import com.kayadami.bouldering.editor.EditorView
import kotlinx.android.synthetic.main.editor_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class EditorFragment : Fragment() {

    private lateinit var fragmentBinding: EditorFragmentBinding
    private val viewModel: EditorViewModel by viewModel()

    private var colorPickerDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        arguments?.let { arguments ->
            val path = EditorFragmentArgs.fromBundle(arguments).imagePath
            val id = EditorFragmentArgs.fromBundle(arguments).boulderingId

            viewModel.load(path, id)
        }

        with(viewModel) {
            finishEditEvent.observe(this@EditorFragment, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    activity?.setResult(Activity.RESULT_OK)
                    navigateUp()
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = EditorFragmentBinding.inflate(inflater, container, false)
        fragmentBinding.viewModel = viewModel
        fragmentBinding.lifecycleOwner = this

        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        buttonColor.setOnClickListener {
            colorPickerDialog = ColorPickerDialogBuilder
                    .with(context, R.style.colorPickerDialog)
                    .initialColor(editorView.color)
                    .wheelType(com.flask.colorpicker.ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(6)
                    .lightnessSliderOnly()
                    .setPositiveButton("OK") { _, selectedColor, _ -> editorView.color = selectedColor }
                    .build()
                    .apply {
                        show()
                    }
        }

        buttonClear.setOnClickListener { editorView.clear() }

        buttonDelete.setOnClickListener { editorView.deleteSelectedHolder() }

        checkNumberedHolder.setOnClickListener {
            viewModel.setOrder(checkNumberedHolder.isChecked)

            editorView.sort()
            editorView.invalidate()
        }

        checkSpecialHolder.setOnClickListener {
            viewModel.setSpecial(checkSpecialHolder.isChecked)

            if (checkSpecialHolder.isChecked) {
                checkNumberedHolder.isChecked = false
            }

            checkNumberedHolder.isEnabled = !checkSpecialHolder.isChecked

            editorView.sort()
            editorView.invalidate()
        }

        editorView.setOnSelectedChangeListener { selectedHolder ->
            viewModel.selectedHolder.value = selectedHolder
        }

        editorView.onProblemListener = object : EditorView.OnProblemListener {
            override fun onLoadingStart() {
                viewModel.isProgress.set(true)
            }

            override fun onLoadingFinish() {
                viewModel.isProgress.set(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.menu_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                navigateUp()

                return true
            }
            R.id.actionDone -> {
                doneEdit()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun doneEdit() = CoroutineScope(Dispatchers.Default).launch {
        viewModel.isProgress.set(true)

        viewModel.store(editorView)

        viewModel.isProgress.set(false)

        finishEdit()
    }

    private suspend fun finishEdit() = withContext(Dispatchers.Main) {
        viewModel.finishEditEvent.value = Event(Unit)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        if (colorPickerDialog?.isShowing == true) {
            colorPickerDialog?.dismiss()
        }

        colorPickerDialog = null
    }
}