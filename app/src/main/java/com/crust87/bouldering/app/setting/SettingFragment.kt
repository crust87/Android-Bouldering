package com.crust87.bouldering.app.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.crust87.bouldering.app.navigate
import com.crust87.bouldering.app.navigateUp
import com.crust87.bouldering.databinding.SettingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: SettingFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            navigateUp()
        }

        binding.buttonOpenSourceLicense.setOnClickListener {
            navigate(
                SettingFragmentDirections.actionSettingFragmentToOpenSourceLicenseFragment()
            )
        }
    }
}