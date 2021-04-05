package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.BinRequest
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.api.request.ReleaseRequest
import io.reactivex.Single
import org.json.JSONObject

class BinRepository(private val apiHelper: ApiHelper) {

    fun submitBin(request: BinRequest): Single<BinResponse> {
        return apiHelper.submitBin(request)
    }

    fun releaseTag(request: ReleaseRequest): Single<JSONObject> {
        return apiHelper.releaseTag(request)
    }
    fun getEntityTags(request: EntityTagRequest): Single<BinResponse> {
        return apiHelper.getEntityTags(request)
    }
}