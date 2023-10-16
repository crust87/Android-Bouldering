package com.crust87.bouldering.app.main

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.crust87.bouldering.R
import com.crust87.bouldering.app.MainFragmentComponent
import com.crust87.bouldering.app.navigate
import com.crust87.bouldering.app.setSupportActionBar
import com.crust87.bouldering.app.supportActionBar
import com.crust87.bouldering.data.bouldering.ListSort
import com.crust87.bouldering.databinding.MainFragmentBinding
import com.crust87.util.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var appBarManager: AppBarManager

    @Inject
    @MainFragmentComponent
    lateinit var layoutManager: StaggeredGridLayoutManager

    @Inject
    lateinit var adapter: MainListAdapter

    lateinit var binding: MainFragmentBinding

    private val viewModel: MainViewModel by viewModels()

    // Working Variable
    var photoPath: String? = null

    private val requestOpenCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val resultCode = it.resultCode
            val path = photoPath

            if (resultCode == Activity.RESULT_OK && path != null) {
                navigate(
                    MainFragmentDirections.actionMainFragmentToEditorFragment()
                        .apply { imagePath = path }
                )

                photoPath = null
            }
        }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.data?.let {
                val path = FileUtil.getRealPathFromURI(requireContext(), it)
                navigate(
                    MainFragmentDirections.actionMainFragmentToEditorFragment()
                        .apply { imagePath = path }
                )
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
                    R.id.actionCamera -> openCamera()
                    R.id.actionGallery -> openGallery()
                    R.id.actionSetting -> navigate(MainFragmentDirections.actionMainFragmentToSettingFragment())
                    R.id.actionSortASC -> viewModel.setSort(ListSort.ASC)
                    R.id.actionSortDESC -> viewModel.setSort(ListSort.DESC)
                }

                return true
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.getItem(0)?.isVisible = !appBarManager.appBarExpanded
                menu.getItem(1)?.isVisible = !appBarManager.appBarExpanded
                menu.getItem(3)?.isVisible = viewModel.uiState.value.sort != ListSort.ASC
                menu.getItem(4)?.isVisible = viewModel.uiState.value.sort != ListSort.DESC
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.appBar.addOnOffsetChangedListener(appBarManager)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        setOrientation(resources.configuration.orientation)

        ViewCompat.requestApplyInsets(binding.layoutContainer)

        binding.buttonCamera.setOnClickListener {
            openCamera()
        }

        binding.buttonGallery.setOnClickListener {
            openGallery()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.mainListUiItems.collectLatest {
                    adapter.setList(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.map { it.sort }.distinctUntilChanged().collectLatest {
                    activity?.invalidateOptionsMenu()
                }
            }
        }

        viewModel.eventChannel.onEach {
            when (it) {
                is OpenViewerEvent -> navigate(
                    MainFragmentDirections.actionMainFragmentToViewerFragment().apply {
                        boulderingId = it.id
                    })
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()

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

    fun openCamera() {
        requestOpenCamera.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            val photoFile =
                FileUtil.createImageFile(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    ?: throw RuntimeException("Feature Not Supported")
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.crust87.bouldering.fileprovider",
                photoFile
            )
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            photoPath = photoFile.absolutePath
        })
    }

    fun openGallery() {
        requestOpenGallery.launch(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        })
    }
}
