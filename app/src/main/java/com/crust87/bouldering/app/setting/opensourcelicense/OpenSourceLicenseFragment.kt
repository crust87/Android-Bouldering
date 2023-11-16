package com.crust87.bouldering.app.setting.opensourcelicense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.crust87.bouldering.app.navigateUp
import com.crust87.bouldering.data.opensource.OpenSourceRepository
import com.crust87.bouldering.databinding.OpenSourceLicenseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OpenSourceLicenseFragment : Fragment() {

    @Inject
    lateinit var adapter: OpenSourceLicenseAdapter

    @Inject
    lateinit var repository: OpenSourceRepository

    lateinit var binding: OpenSourceLicenseFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = OpenSourceLicenseFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            navigateUp()
        }

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            try {
                adapter.list = repository.getList()
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
