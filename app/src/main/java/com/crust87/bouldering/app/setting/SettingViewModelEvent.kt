package com.crust87.bouldering.app.setting

sealed interface SettingViewModelEvent

object OpenOpensourceLicenseEvent: SettingViewModelEvent

object NavigateUpEvent : SettingViewModelEvent