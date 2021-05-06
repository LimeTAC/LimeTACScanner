package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.api.request.ForkliftRequest
import com.limetac.scanner.data.api.request.ReleaseTagRequest
import com.limetac.scanner.data.model.ReleaseTagResponse
import io.reactivex.Single

class AntennaRepository(private val apiHelper: ApiHelper) {
    fun submitForklift(request: ForkliftRequest): Single<BinResponse> {
        return apiHelper.submitForklift(request)
    }

    fun getEntityTags(request: EntityTagRequest): Single<BinResponse> {
        return apiHelper.getEntityTags(request)
    }

    fun releaseTag(request: ReleaseTagRequest): Single<ReleaseTagResponse> {
        return apiHelper.releaseTag(request)
    }
}