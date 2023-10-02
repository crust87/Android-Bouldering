package com.kayadami.bouldering.data.opensource

import com.kayadami.bouldering.data.opensource.type.OpenSourceLicense

interface OpenSourceDataSource {

    fun getList(): List<OpenSourceLicense>
}