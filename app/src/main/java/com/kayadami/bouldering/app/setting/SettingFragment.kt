package com.kayadami.bouldering.app.setting

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.databinding.SettingFragmentBinding
import com.kayadami.bouldering.utils.PermissionChecker
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import kotlinx.android.synthetic.main.setting_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class SettingFragment : Fragment() {

    private lateinit var fragmentBinding: SettingFragmentBinding
    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionChecker.REQUEST_EXPORT) {
                exportAll()
            } else if (requestCode == PermissionChecker.REQUEST_IMPORT) {
                importAll()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun exportAll() {
        context?.let {
            if (PermissionChecker.check(it)) {
                viewModel.exportAll()
            } else {
                PermissionChecker.request(this@SettingFragment, PermissionChecker.REQUEST_EXPORT)
            }
        }
    }

    private fun importAll() {
        context?.let {
            if (PermissionChecker.check(it)) {
                viewModel.importAll()
            } else {
                PermissionChecker.request(this@SettingFragment, PermissionChecker.REQUEST_IMPORT)
            }
        }
    }

    private fun openOpenSourceLicenseFragment() {
        SettingFragmentDirections.actionSettingFragmentToOpenSourceLicenseFragment().also {
            navigate(it)
        }
    }
}