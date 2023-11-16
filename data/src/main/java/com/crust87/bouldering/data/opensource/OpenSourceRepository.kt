package com.crust87.bouldering.data.opensource

import com.crust87.bouldering.data.opensource.type.OpenSourceLicense

class OpenSourceRepository(val boulderingService: BoulderingService) {

    suspend fun getList(): List<OpenSourceLicense> {
//        return listOf(
//            OpenSourceLicense(
//                "Android Architecture Blueprints",
//                "https://github.com/googlesamples/android-architecture",
//                "Copyright 2019 Google Inc.\nApache License, Version 2.0"
//            ),
//            OpenSourceLicense(
//                "PhotoView",
//                "https://github.com/chrisbanes/PhotoView",
//                "Copyright 2011, 2012 Chris Banes.\nApache License, Version 2.0"
//            ),
//            OpenSourceLicense(
//                "Color Picker",
//                "https://github.com/QuadFlask/colorpicker",
//                "Copyright 2014-2017 QuadFlask.\nApache License, Version 2.0"
//            ),
//        )
        return boulderingService.listOpenSource("android")
    }
}