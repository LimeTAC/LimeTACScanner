package com.limetac.scanner.data.api.request

import com.google.gson.annotations.SerializedName

data class ReleaseTagRequest(

    @field:SerializedName("password")
    val password: String = "!5353ns",

    @field:SerializedName("tagCode")
    var tagCode: String? = null,

    @field:SerializedName("entityId")
    var entityId: Long? = null,

    @field:SerializedName("username")
    val username: String = "eventreviewer1",

    @field:SerializedName("entityType")
    var entityType: Int? = null
)
