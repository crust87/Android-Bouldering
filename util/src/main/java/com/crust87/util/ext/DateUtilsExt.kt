package com.crust87.util.ext

import com.crust87.util.DateUtils

fun Long.asDateText() = DateUtils.convertDate(this)