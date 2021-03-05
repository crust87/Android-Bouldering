package com.kayadami.bouldering.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat

class PermissionChecker2(
        val context: Context
) {

    fun checkWrite(): Boolean {
        val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return write == PackageManager.PERMISSION_GRANTED
    }

    fun checkRead(): Boolean {
        val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

        return read == PackageManager.PERMISSION_GRANTED
    }

    fun checkReadWrite(): Boolean {
        val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        fun Map<String, Boolean>.isAllGranted() = entries.map { it.value }.all { it }
    }
}
