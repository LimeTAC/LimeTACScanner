package com.limetac.scanner.data.model

import com.google.gson.annotations.SerializedName

data class ScanHelperRequest(

	@field:SerializedName("tagCodes")
	var tagCodes: ArrayList<String?>? = null,

	@field:SerializedName("password")
	val password: String = "!5353ns",

	@field:SerializedName("locationCode")
    var locationCode: String? = null,

	@field:SerializedName("username")
	val username: String = "eventreviewer1"
)
