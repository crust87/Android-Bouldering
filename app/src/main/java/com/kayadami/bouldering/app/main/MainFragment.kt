package com.kayadami.bouldering.app.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigate
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.constants.RequestCode
import com.kayadami.bouldering.databinding.MainFragmentBinding
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.list.GridSpacingItemDecoration
import com.kayadami.bouldering.utils.FileUtil
import com.kayadami.bouldering.image.FragmentImageLoader
import com.kayadami.bouldering.image.ImageLoader
import com.kayadami.bouldering.utils.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    @FragmentImageLoader
    lateinit var imageLoader: ImageLoader

    private lateinit var fragmentBinding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarManager: AppBarManager
    private val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    private val itemDecoration = GridSpacingItemDecoration(2)

    private lateinit var adapter: BoulderingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        itemDecoration.spacing = resources.getDimension(R.dimen.list_spacing).toInt()
        appBarManager = AppBarManager(activity)
        appBarManager.offsetBound = resources.getDimension(R.dimen.header_height) / 2

        adapter = BoulderingAdapter(viewModel, imageLoader)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = MainFragmentBinding.inflate(inflater, container, false)
        fragmentBinding.viewModel = viewModel
        fragmentBinding.lifecycleOwner = this

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openBoulderingEvent.observe(viewLifecycleOwner) {
            if (it >= 0) {
                openViewer(viewModel[it])
            }
        }

        viewModel.openCameraEvent.observe(viewLifecycleOwner) {
            this@MainFragment.openCamera()
        }

        viewModel.openGalleryEvent.observe(viewLifecycleOwner) {
            this@MainFragment.openGallery()
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.adapter = adapter

        appBar.addOnOffsetChangedListener(appBarManager)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        setOrientation(resources.configuration.orientation)
    }

    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        recyclerView.layoutManager = null
        recyclerView.removeItemDecoration(itemDecoration)
        appBar.removeOnOffsetChangedListener(appBarManager)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.getItem(0)?.isVisible = !appBarManager.appBarExpanded
        menu.getItem(1)?.isVisible = !appBarManager.appBarExpanded

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSetting -> openSetting()
            R.id.actionCamera -> viewModel.openCamera()
            R.id.actionGallery -> viewModel.openGallery()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            viewModel.photoPath?.let {
                openEditor(it)
                viewModel.photoPath = null
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val context = context

            if (context != null) {
                data?.data?.let {
                    val path = FileUtil.getRealPathFromURI(context, it)
                    openEditor(path)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionChecker.REQUEST_CAMERA) {
                viewModel.openCamera()
            } else if (requestCode == PermissionChecker.REQUEST_GALLERY) {
                viewModel.openGallery()
            }
        }
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
            boulderingId = data.createdDate
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
        val activity = activity
        activity ?: return

        if (PermissionChecker.check(activity)) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).run {
                resolveActivity(activity.packageManager) ?: return
                val photoFile = FileUtil.createImageFile(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)) ?: return
                val photoURI = FileProvider.getUriForFile(activity, "com.kayadami.bouldering.fileprovider", photoFile)
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                viewModel.photoPath = photoFile.absolutePath
                startActivityForResult(this, RequestCode.REQUEST_CODE_CAMERA)
            }
        } else {
            PermissionChecker.request(this@MainFragment, PermissionChecker.REQUEST_CAMERA)
        }
    }

    private fun openGallery() {
        val activity = activity
        activity ?: return

        if (PermissionChecker.check(activity)) {
            Intent(Intent.ACTION_PICK).run {
                type = "image/*"
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(this, RequestCode.REQUEST_CODE_GALLERY)
            }
        } else {
            PermissionChecker.request(this@MainFragment, PermissionChecker.REQUEST_GALLERY)
        }
    }
}
