package com.limetac.scanner.data.api

import com.limetac.scanner.data.api.request.*
import com.limetac.scanner.data.model.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject

interface ApiService {

    fun getUsers(): Single<List<User>>

    fun getTagsByPkg(request: PkgRequest): Single<PkgDetails>

    fun releasePkgTag(request: PkgRequest): Single<JSONObject>

    fun getTagsByTags(request: PkgRequest): Single<PkgDetails>

    fun submitPkg(request: AddPkgRequest): Single<PkgDetails>

    fun getPackagingItems(request: PkgRequest): Observable<List<PackagingItem>>

    fun submitBin(request: BinRequest): Single<BinResponse>

    fun releaseTag(request: ReleaseTagRequest): Single<ReleaseTagResponse>

    fun getEntityTag(request: EntityTagRequest): Single<BinResponse>

    fun submitForkLift(request: ForkliftRequest): Single<BinResponse>

    fun getEntityByTag(request: EntityTagRequest): Single<List<BinResponse>>

    fun createLocationHelper(request: ScanHelperRequest): Single<BinResponse>

}