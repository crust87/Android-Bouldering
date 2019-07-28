package com.kayadami.bouldering.app

import android.content.Context
import android.view.WindowManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.editor.EditorResourceManager
import com.kayadami.bouldering.app.editor.EditorViewModel
import com.kayadami.bouldering.app.main.MainViewModel
import com.kayadami.bouldering.app.setting.SettingViewModel
import com.kayadami.bouldering.app.viewer.ViewerViewModel
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.data.BoulderingRepository
import com.kayadami.bouldering.data.PreferencesDataSource
import com.kayadami.bouldering.data.StorageDataSource
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.utils.ImageLoader
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object Injection {

    fun get(context: Context): Module {
        return module {
            single { PreferencesDataSource(get()) }
            single { StorageDataSource(get()) }
            single { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
            single { BoulderingRepository(get(), get()) } bind BoulderingDataSource::class
            single { ImageGenerator(get()) }
            single { RoundedCornersTransformation(context.resources.getDimension(R.dimen.list_round).toInt(), 0) }
            single { ImageLoader(get()) }

            single { EditorResourceManager(context)}

            viewModel { EditorViewModel(get(), get()) }
            viewModel { MainViewModel(get(), get()) }
            viewModel { SettingViewModel(get(), get()) }
            viewModel { ViewerViewModel(get(), get()) }
        }
    }
}