package com.kayadami.bouldering.app.main

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.MainFragmentComponent
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.databinding.MainFragmentBinding
import com.kayadami.bouldering.image.FragmentImageLoader
import com.kayadami.bouldering.image.ImageLoader
import com.kayadami.bouldering.utils.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    @FragmentImageLoader
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var appBarManager: AppBarManager

    @Inject
    @MainFragmentComponent
    lateinit var layoutManager: StaggeredGridLayoutManager

    @Inject
    lateinit var adapter: BoulderingAdapter

    lateinit var binding: MainFragmentBinding

    private val viewModel: MainViewModel by viewModels()

    private val requestOpenCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val resultCode = it.resultCode
            val path = viewModel.photoPath

            if (resultCode == Activity.RESULT_OK && path != null) {
                viewModel.openEditor(path)
                viewModel.photoPath = null
            }
        }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.data?.let {
                val path = FileUtil.getRealPathFromURI(requireContext(), it)
                viewModel.openEditor(path)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.actionCamera -> viewModel.openCamera()
                    R.id.actionGallery -> viewModel.openGallery()
                    R.id.actionSetting -> viewModel.openSetting()
                    R.id.actionSortASC -> viewModel.setSort(BoulderingDataSource.ListSort.ASC)
                    R.id.actionSortDESC -> viewModel.setSort(BoulderingDataSource.ListSort.DESC)
                }

                return true
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.getItem(0)?.isVisible = !appBarManager.appBarExpanded
                menu.getItem(1)?.isVisible = !appBarManager.appBarExpanded
                menu.getItem(3)?.isVisible = viewModel.listSort.value != BoulderingDataSource.ListSort.ASC
                menu.getItem(4)?.isVisible = viewModel.listSort.value != BoulderingDataSource.ListSort.DESC
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setBoulderingItemEventListener {
            viewModel.openViewer(it)
        }

        viewModel.list.observe(viewLifecycleOwner) {
            adapter.setList(it)
        }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.appBar.addOnOffsetChangedListener(appBarManager)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        setOrientation(resources.configuration.orientation)

        ViewCompat.requestApplyInsets(binding.layoutContainer)

        viewModel.eventChannel.onEach {
            when (it) {
                is OpenSettingEvent -> navigate(MainFragmentDirections.actionMainFragmentToSettingFragment())
                is OpenViewerEvent -> navigate(
                    MainFragmentDirections.actionMainFragmentToViewerFragment().apply {
                        boulderingId = it.data.id
                    })

                is OpenEditorEvent -> navigate(
                    MainFragmentDirections.actionMainFragmentToEditorFragment()
                        .apply { imagePath = it.path })

                is OpenCameraEvent -> requestOpenCamera.launch(it.intent)
                is OpenGalleryEvent -> requestOpenGallery.launch(it.intent)
                is ListSortChangeEvent -> activity?.invalidateOptionsMenu()
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        lifecycleScope.coroutineContext.cancelChildren()

        binding.recyclerView.layoutManager = null
        binding.appBar.removeOnOffsetChangedListener(appBarManager)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        setOrientation(newConfig.orientation)
    }

    private fun setOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.spanCount = 4
        } else {
            layoutManager.spanCount = 2
        }
    }
}
