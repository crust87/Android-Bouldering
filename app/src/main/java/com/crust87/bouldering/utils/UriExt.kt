package com.crust87.bouldering.utils

import android.content.Intent
import android.net.Uri

fun Uri.toShareIntent(): Intent {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = "image/*"

    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.putExtra(Intent.EXTRA_SUBJECT, "")
    intent.putExtra(Intent.EXTRA_TEXT, "")
    intent.putExtra(Intent.EXTRA_STREAM, this)

    return Intent.createChooser(intent, "Share Image")
}