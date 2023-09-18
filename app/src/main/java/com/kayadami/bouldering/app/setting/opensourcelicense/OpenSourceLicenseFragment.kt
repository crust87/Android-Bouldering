package com.kayadami.bouldering.app.setting.opensourcelicense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.databinding.OpenSourceLicenseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class OpenSourceLicenseFragment : Fragment() {

    @Inject
    lateinit var repository: BoulderingDataSource

    lateinit var fragmentBinding: OpenSourceLicenseFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = OpenSourceLicenseFragmentBinding.inflate(inflater, container, false)

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSupportActionBar(fragmentBinding.toolbar)
        supportActionBar?.run {
            setTitle(R.string.open_source_license)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        fragmentBinding.recyclerView.adapter = OpenSourceLicenseAdapter(getOpenSourceList())
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

    fun getOpenSourceList(): List<OpenSourceLicense> {
        return ArrayList<OpenSourceLicense>().apply {
            add(
                OpenSourceLicense(
                    "Android Architecture Blueprints",
                    "https://github.com/googlesamples/android-architecture",
                    "Copyright 2019 Google Inc.\nApache License, Version 2.0"
                )
            )

            add(
                OpenSourceLicense(
                    "PhotoView",
                    "https://github.com/chrisbanes/PhotoView",
                    "Copyright 2011, 2012 Chris Banes.\nApache License, Version 2.0"
                )
            )

            add(
                OpenSourceLicense(
                    "Color Picker",
                    "https://github.com/QuadFlask/colorpicker",
                    "Copyright 2014-2017 QuadFlask.\nApache License, Version 2.0"
                )
            )
        }
    }
}
