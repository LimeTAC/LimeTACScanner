package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.*
import com.limetac.scanner.data.model.ReleaseTagResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject

class BinRepository(private val apiHelper: ApiHelper) {

    fun submitBin(request: BinRequest): Single<BinResponse> {
        return apiHelper.submitBin(request)
    }

    fun releaseTag(request: ReleaseTagRequest): Single<ReleaseTagResponse> {
        return apiHelper.releaseTag(request)
    }
    fun getEntityTags(request: EntityTagRequest): Single<BinResponse> {
        return apiHelper.getEntityTags(request)
    }
}