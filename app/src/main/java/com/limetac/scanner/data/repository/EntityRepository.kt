package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import io.reactivex.Single

class EntityRepository(private val apiHelper: ApiHelper) {


    fun getEntityByTagCode(request: EntityTagRequest): Single<List<BinResponse>> {
        return apiHelper.getEntityByTagCode(request)
    }

    fun getTagEntity(request: EntityTagRequest): Single<BinResponse> {
        return apiHelper.getEntityTags(request)
    }
}