package com.kayadami.bouldering.app.setting.opensourcelicense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.navigateUp
import com.kayadami.bouldering.app.setSupportActionBar
import com.kayadami.bouldering.app.supportActionBar
import com.kayadami.bouldering.data.BoulderingDataSource
import kotlinx.android.synthetic.main.open_source_license_fragment.*
import javax.inject.Inject

class OpenSourceLicenseFragment : Fragment() {

    @Inject
    lateinit var repository: BoulderingDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.open_source_license_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setTitle(R.string.open_source_license)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = OpenSourceLicenseAdapter(repository.getOpenSourceList())
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
}
