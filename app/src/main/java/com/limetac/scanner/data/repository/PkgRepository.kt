package com.limetac.scanner.data.repository

import com.google.gson.JsonObject
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.request.*
import com.limetac.scanner.data.model.PackagingItem
import com.limetac.scanner.data.model.PkgDetails
import com.limetac.scanner.data.model.ReleaseTagResponse
import com.limetac.scanner.data.model.User
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject

class PkgRepository(private val apiHelper: ApiHelper) {

    fun getTagsByPkg(request: PkgRequest): Single<PkgDetails> {
        return apiHelper.getTagsByPkg(request)
    }

  /*  fun releaseTag(request: PkgRequest): Single<JSONObject> {
        return apiHelper.releasePackageTag(request)
    }*/

    fun releaseTag(request: ReleaseTagRequest): Single<ReleaseTagResponse> {
        return apiHelper.releaseTag(request)
    }

    fun getTagsByTag(request:PkgRequest): Single<PkgDetails> {
        return apiHelper.getTagsByTag(request)}

    fun submitPkg(request:AddPkgRequest): Single<PkgDetails> {
        return apiHelper.submitPkg(request)}

    fun getPackagingItem(request:PkgRequest): Observable<List<PackagingItem>> {
        return apiHelper.getPackagingItems(request)}

    fun getEntityByTagCode(request: EntityTagRequest): Single<List<BinResponse>> {
        return apiHelper.getEntityByTagCode(request)
    }
}