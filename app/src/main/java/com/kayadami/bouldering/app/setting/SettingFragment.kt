package com.kayadami.bouldering.app.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.databinding.SettingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.setting_fragment.*

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var fragmentBinding: SettingFragmentBinding
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = SettingFragmentBinding.inflate(inflater, container, false)
        fragmentBinding.viewModel = viewModel
        fragmentBinding.lifecycleOwner = this

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setTitle(R.string.setting)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        buttonExport.setOnClickListener {
            exportAll()
        }

        buttonImport.setOnClickListener {
            importAll()
        }

        buttonOpenSourceLicense.setOnClickListener {
            openOpenSourceLicenseFragment()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateUp()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    val exportPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
        val granted = permissions.entries.map {
            it.value
        }.all {
            it
        }

        if (granted) {
            exportAll()
        }
    }
    val importPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
        val granted = permissions.entries.map {
            it.value
        }.all {
            it
        }

        if (granted) {
            importAll()
        }
    }

    fun checkPermission(): Boolean {
        val read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
    }

    private fun exportAll() {
        context?.let {
            if (checkPermission()) {
                viewModel.exportAll()
            } else {
                exportPermissionLauncher.launch(
                        arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                )
            }
        }
    }

    private fun importAll() {
        context?.let {
            if (checkPermission()) {
                viewModel.importAll()
            } else {
                importPermissionLauncher.launch(
                        arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                )
            }
        }
    }

    private fun openOpenSourceLicenseFragment() {
        SettingFragmentDirections.actionSettingFragmentToOpenSourceLicenseFragment().also {
            navigate(it)
        }
    }
}