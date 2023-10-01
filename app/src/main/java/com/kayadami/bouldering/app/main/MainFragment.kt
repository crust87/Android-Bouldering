package com.kayadami.bouldering.app.main

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.MainFragmentComponent
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.data.type.Bouldering
import com.kayadami.bouldering.databinding.MainFragmentBinding
import com.kayadami.bouldering.image.FragmentImageLoader
import com.kayadami.bouldering.image.ImageLoader
import com.kayadami.bouldering.list.GridSpacingItemDecoration
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
    @MainFragmentComponent
    lateinit var itemDecoration: GridSpacingItemDecoration

    @Inject
    lateinit var adapter: BoulderingAdapter

    lateinit var binding: MainFragmentBinding

    private val viewModel: MainViewModel by viewModels()

    var photoPath: String? = null

    private val requestOpenCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val resultCode = it.resultCode
            val path = photoPath

            if (resultCode == Activity.RESULT_OK && path != null) {
                viewModel.openEditor(path)
                photoPath = null
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
                    R.id.actionSetting -> viewModel.openSetting()
                    R.id.actionCamera -> viewModel.openCamera()
                    R.id.actionGallery -> viewModel.openGallery()
                }

                return true
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.getItem(0)?.isVisible = !appBarManager.appBarExpanded
                menu.getItem(1)?.isVisible = !appBarManager.appBarExpanded
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
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.recyclerView.adapter = adapter

        binding.appBar.addOnOffsetChangedListener(appBarManager)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        setOrientation(resources.configuration.orientation)

        ViewCompat.requestApplyInsets(binding.layoutContainer)

        viewModel.eventChannel.onEach {
            when (it) {
                is OpenSettingEvent -> openSetting()
                is OpenViewerEvent -> openViewer(it.data)
                is OpenEditorEvent -> openEditor(it.path)
                is OpenCameraEvent -> openCamera()
                is OpenGalleryEvent -> openGallery()
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        lifecycleScope.coroutineContext.cancelChildren()

        binding.recyclerView.layoutManager = null
        binding.recyclerView.removeItemDecoration(itemDecoration)
        binding.appBar.removeOnOffsetChangedListener(appBarManager)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        setOrientation(newConfig.orientation)
    }

    private fun setOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.spanCount = 4
            itemDecoration.spanCount = 4
        } else {
            layoutManager.spanCount = 2
            itemDecoration.spanCount = 2
        }

        adapter.notifyDataSetChanged()
    }

    private fun openSetting() {
        MainFragmentDirections.actionMainFragmentToSettingFragment().also {
            navigate(it)
        }
    }

    private fun openViewer(data: Bouldering) {
        MainFragmentDirections.actionMainFragmentToViewerFragment().apply {
            boulderingId = data.id
        }.also {
            navigate(it)
        }
    }

    private fun openEditor(path: String) {
        MainFragmentDirections.actionMainFragmentToEditorFragment().apply {
            imagePath = path
        }.also {
            navigate(it)
        }
    }

    private fun openCamera() {
        requestOpenCamera.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            val photoFile =
                FileUtil.createImageFile(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    ?: return
            val photoURI = FileProvider.getUriForFile(
                requireActivity(),
                "com.kayadami.bouldering.fileprovider",
                photoFile
            )
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            photoPath = photoFile.absolutePath
        })
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestOpenGallery.launch(Intent(MediaStore.ACTION_PICK_IMAGES))
        } else {
            requestOpenGallery.launch(Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }
}
