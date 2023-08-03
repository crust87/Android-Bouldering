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

    lateinit var binding: EditorFragmentBinding

    val viewModel: EditorViewModel by viewModels()

    val aras: EditorFragmentArgs by navArgs()

    var colorPickerDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = EditorFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@EditorFragment.viewModel
            lifecycleOwner = this@EditorFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.toolbar) {
            inflateMenu(R.menu.menu_editor)
            setNavigationIcon(R.drawable.ic_back)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionDone -> {
                        viewModel.done(binding.editorView)

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
            binding.editorView.sort()
            binding.editorView.invalidate()
        }

        viewModel.openColorChooserEvent.observe(viewLifecycleOwner) {
            colorPickerDialog = ColorPickerDialogBuilder
                    .with(context, R.style.colorPickerDialog)
                    .initialColor(binding.editorView.color)
                    .wheelType(com.flask.colorpicker.ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(6)
                    .lightnessSliderOnly()
                    .setPositiveButton("OK") { _, selectedColor, _ -> binding.editorView.color = selectedColor }
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

        viewModel.init(aras.imagePath, aras.boulderingId)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (colorPickerDialog?.isShowing == true) {
            colorPickerDialog?.dismiss()
        }

        colorPickerDialog = null
    }
}