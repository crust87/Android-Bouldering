package com.kayadami.bouldering.app.setting

sealed interface SettingViewModelEvent

object OpenOpensourceLicenseEvent: SettingViewModelEvent

object NavigateUpEvent : SettingViewModelEvent