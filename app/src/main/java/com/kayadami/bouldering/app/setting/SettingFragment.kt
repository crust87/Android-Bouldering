package com.kayadami.bouldering.app.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            navigate(
                SettingFragmentDirections.actionSettingFragmentToOpenSourceLicenseFragment()
            )
        }
    }
}