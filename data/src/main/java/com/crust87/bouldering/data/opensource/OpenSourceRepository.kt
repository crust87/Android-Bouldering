package com.crust87.bouldering.data.opensource

import com.crust87.bouldering.data.opensource.type.OpenSourceLicense

class OpenSourceRepository(
    val boulderingService: BoulderingService
) {

    suspend fun getList(): List<OpenSourceLicense> {
        return boulderingService.listOpenSource("android")
    }
}