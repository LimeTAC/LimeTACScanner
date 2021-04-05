package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.BinRequest
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.api.request.ReleaseRequest
import io.reactivex.Single
import org.json.JSONObject

class ScanHelperRepository(private val apiHelper: ApiHelper) {

    fun createScanHelper(request: BinRequest): Single<BinResponse> {
        return apiHelper.createLocationHelper(request)
    }

    fun getEntityDetail(request: EntityTagRequest): Single<BinResponse> {
        return apiHelper.getEntityTags(request)
    }

}