package com.crust87.bouldering.app.editor

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.navArgs
import com.crust87.bouldering.editor.EditorView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.crust87.bouldering.R
import com.crust87.bouldering.app.navigateUp
import com.crust87.bouldering.data.asEditorBouldering
import com.crust87.bouldering.databinding.EditorFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

        viewModel.init(aras.imagePath, aras.boulderingId)

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

        binding.editorView.setOnSelectedChangeListener {
            viewModel.setHolder(it)
        }

        viewModel.eventChannel.onEach {
            when(it) {
                is OpenColorPickerEvent -> openColorPicker()
                is NavigateUpEvent -> navigateUp()
                is ToastEvent -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (colorPickerDialog?.isShowing == true) {
            colorPickerDialog?.dismiss()
        }

        colorPickerDialog = null
    }

    private fun openColorPicker() {
        colorPickerDialog = ColorPickerDialogBuilder
            .with(context, R.style.colorPickerDialog)
            .initialColor(binding.editorView.color)
            .wheelType(com.flask.colorpicker.ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(6)
            .lightnessSliderOnly()
            .setPositiveButton("OK") { _, selectedColor, _ -> binding.editorView.color = selectedColor }
            .build()
            .apply {
                setOnDismissListener {
                    colorPickerDialog = null
                }

                show()
            }
    }
}