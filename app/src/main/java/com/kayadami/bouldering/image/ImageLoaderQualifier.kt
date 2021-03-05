package com.kayadami.bouldering.image

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationImageLoader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ActivityImageLoader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FragmentImageLoader