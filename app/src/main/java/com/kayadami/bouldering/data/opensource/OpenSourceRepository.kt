package com.kayadami.bouldering.data.opensource

import com.kayadami.bouldering.data.opensource.type.OpenSourceLicense
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenSourceRepository @Inject constructor() {

    fun getList(): List<OpenSourceLicense> {
        return listOf(
            OpenSourceLicense(
                "Android Architecture Blueprints",
                "https://github.com/googlesamples/android-architecture",
                "Copyright 2019 Google Inc.\nApache License, Version 2.0"
            ),
            OpenSourceLicense(
                "PhotoView",
                "https://github.com/chrisbanes/PhotoView",
                "Copyright 2011, 2012 Chris Banes.\nApache License, Version 2.0"
            ),
            OpenSourceLicense(
                "Color Picker",
                "https://github.com/QuadFlask/colorpicker",
                "Copyright 2014-2017 QuadFlask.\nApache License, Version 2.0"
            ),
        )
    }
}