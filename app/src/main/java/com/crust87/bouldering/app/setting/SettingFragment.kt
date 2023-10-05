package com.crust87.bouldering.app.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.crust87.bouldering.app.navigate
import com.crust87.bouldering.app.navigateUp
import com.crust87.bouldering.databinding.SettingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: SettingFragmentBinding

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@SettingFragment.viewModel
            lifecycleOwner = this@SettingFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.eventChannel.onEach {
            when(it) {
                OpenOpensourceLicenseEvent -> navigate(
                    SettingFragmentDirections.actionSettingFragmentToOpenSourceLicenseFragment()
                )
                NavigateUpEvent -> navigateUp()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}