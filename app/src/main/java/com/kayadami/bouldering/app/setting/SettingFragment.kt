package com.kayadami.bouldering.app.setting

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.databinding.SettingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var fragmentBinding: SettingFragmentBinding

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = SettingFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@SettingFragment.viewModel
            lifecycleOwner = this@SettingFragment

        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentBinding.toolbar.setTitle(R.string.setting)
        fragmentBinding.toolbar.setNavigationIcon(R.drawable.ic_back)
        fragmentBinding.toolbar.setNavigationOnClickListener {
            navigateUp()
        }

        fragmentBinding.buttonOpenSourceLicense.setOnClickListener {
            openOpenSourceLicenseFragment()
        }

        viewModel.requestExportPermissionEvent.observe(viewLifecycleOwner) {
            exportPermissionLauncher.launch(
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            )
        }

        viewModel.requestImportPermissionEvent.observe(viewLifecycleOwner) {
            importPermissionLauncher.launch(
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            )
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun Map<String, Boolean>.isAllGranted() = entries.map { it.value }.all { it }

    val exportPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.isAllGranted()) {
            viewModel.exportAll()
        } else {
            viewModel.toastEvent.value = "NO PERMISSIONS!"
        }
    }

    val importPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.isAllGranted()) {
            viewModel.importAll()
        } else {
            viewModel.toastEvent.value = "NO PERMISSIONS!"
        }
    }

    private fun openOpenSourceLicenseFragment() {
        navigate(
                SettingFragmentDirections.actionSettingFragmentToOpenSourceLicenseFragment()
        )
    }
}