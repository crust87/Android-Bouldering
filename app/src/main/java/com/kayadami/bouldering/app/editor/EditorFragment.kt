package com.kayadami.bouldering.app.editor

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.databinding.EditorFragmentBinding
import kotlinx.android.synthetic.main.editor_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class EditorFragment : Fragment() {

    private lateinit var fragmentBinding: EditorFragmentBinding
    private val viewModel: EditorViewModel by viewModel()

    private var colorPickerDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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

        arguments?.let { arguments ->
            val path = EditorFragmentArgs.fromBundle(arguments).imagePath
            val id = EditorFragmentArgs.fromBundle(arguments).boulderingId

            viewModel.load(path, id)
        }

        viewModel.finishEditEvent.observe(viewLifecycleOwner) {
            activity?.setResult(Activity.RESULT_OK)
            navigateUp()
        }

        viewModel.openColorChooserEvent.observe(viewLifecycleOwner) {
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

        viewModel.errorEvent.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateUp()

                return true
            }
            R.id.actionDone -> {
                viewModel.done(editorView)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (colorPickerDialog?.isShowing == true) {
            colorPickerDialog?.dismiss()
        }

        colorPickerDialog = null
    }
}