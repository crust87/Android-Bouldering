package com.kayadami.bouldering.app.editor

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.databinding.EditorFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditorFragment : Fragment() {

    private lateinit var fragmentBinding: EditorFragmentBinding

    private val viewModel: EditorViewModel by viewModels()

    private var colorPickerDialog: AlertDialog? = null

    val aras: EditorFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = EditorFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@EditorFragment.viewModel
            lifecycleOwner = this@EditorFragment
        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(fragmentBinding.toolbar) {
            inflateMenu(R.menu.menu_editor)
            setNavigationIcon(R.drawable.ic_back)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionDone -> {
                        viewModel.done(fragmentBinding.editorView)

                        true
                    }
                    else -> false
                }
            }

            setNavigationOnClickListener {
                navigateUp()
            }
        }

        viewModel.sortEvent.observe(viewLifecycleOwner) {
            fragmentBinding.editorView.sort()
            fragmentBinding.editorView.invalidate()
        }

        viewModel.openColorChooserEvent.observe(viewLifecycleOwner) {
            colorPickerDialog = ColorPickerDialogBuilder
                    .with(context, R.style.colorPickerDialog)
                    .initialColor(fragmentBinding.editorView.color)
                    .wheelType(com.flask.colorpicker.ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(6)
                    .lightnessSliderOnly()
                    .setPositiveButton("OK") { _, selectedColor, _ -> fragmentBinding.editorView.color = selectedColor }
                    .build()
                    .apply {
                        show()
                    }
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateUpEvent.observe(viewLifecycleOwner) {
            navigateUp()
        }

        viewModel.load(aras.imagePath, aras.boulderingId)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (colorPickerDialog?.isShowing == true) {
            colorPickerDialog?.dismiss()
        }

        colorPickerDialog = null
    }
}