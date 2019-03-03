package com.kayadami.bouldering.app

import android.content.Context
import android.view.WindowManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.editor.EditorViewModel
import com.kayadami.bouldering.app.main.MainViewModel
import com.kayadami.bouldering.app.setting.SettingViewModel
import com.kayadami.bouldering.app.viewer.ViewerViewModel
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.data.BoulderingTestRepository
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.utils.ImageLoader
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

object Injection {

    fun get(context: Context): Module {
        return module {
            single { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
            single { BoulderingTestRepository(get()) } bind BoulderingDataSource::class
            single { ImageGenerator(get()) }
            single { RoundedCornersTransformation(context.resources.getDimension(R.dimen.list_round).toInt(), 0) }
            single { ImageLoader(get()) }

            viewModel { EditorViewModel(get(), get()) }
            viewModel { MainViewModel(get(), get()) }
            viewModel { SettingViewModel(get(), get()) }
            viewModel { ViewerViewModel(get(), get(), get()) }
        }
    }
}