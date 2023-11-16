package com.crust87.bouldering.app.setting.opensourcelicense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.crust87.bouldering.app.navigateUp
import com.crust87.bouldering.app.viewer.comment.type.CommentItemUIState
import com.crust87.bouldering.data.COMMENT_PAGE_LIMIT
import com.crust87.bouldering.data.opensource.OpenSourceRepository
import com.crust87.bouldering.databinding.OpenSourceLicenseFragmentBinding
import com.crust87.util.ext.asDateText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OpenSourceLicenseFragment : Fragment() {

    @Inject
    lateinit var adapter: OpenSourceLicenseAdapter

    @Inject
    lateinit var pagingSource: OpenSourceLicensePagingSource

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

        viewLifecycleOwner.lifecycleScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = Int.MAX_VALUE,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { pagingSource }
            ).flow.cachedIn(lifecycleScope).collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

    }
}
