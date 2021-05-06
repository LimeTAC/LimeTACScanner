package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.*
import com.limetac.scanner.data.model.ReleaseTagResponse
import com.limetac.scanner.data.model.ScanHelperRequest
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject

class ScanHelperRepository(private val apiHelper: ApiHelper) {

    fun createScanHelper(request: ScanHelperRequest): Single<BinResponse> {
        return apiHelper.createLocationHelper(request)
    }

    fun getEntityDetail(request: EntityTagRequest): Single<BinResponse> {
        return apiHelper.getEntityTags(request)
    }

    fun releaseTag(request: ReleaseTagRequest): Single<ReleaseTagResponse> {
        return apiHelper.releaseTag(request)
    }
}