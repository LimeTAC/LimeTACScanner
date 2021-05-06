package com.limetac.scanner.data.api

import com.limetac.scanner.data.api.request.*
import com.limetac.scanner.data.model.ScanHelperRequest

class ApiHelper(private val apiService: ApiService) {

    fun getUsers() = apiService.getUsers()
    fun getTagsByPkg(request: PkgRequest) = apiService.getTagsByPkg(request)
    fun getTagsByTag(request: PkgRequest) = apiService.getTagsByTags(request)
    fun submitPkg(request: AddPkgRequest) = apiService.submitPkg(request)
    fun releasePackageTag(request: PkgRequest) = apiService.releasePkgTag(request)
    fun getPackagingItems(request: PkgRequest) = apiService.getPackagingItems(request)
    fun submitBin(request: BinRequest) = apiService.submitBin(request)
    fun releaseTag(request: ReleaseTagRequest) = apiService.releaseTag(request)
    fun submitForklift(request: ForkliftRequest) = apiService.submitForkLift(request)
    fun getEntityTags(request: EntityTagRequest) = apiService.getEntityTag(request)
    fun getEntityByTagCode(request: EntityTagRequest) = apiService.getEntityByTag(request)
    fun createLocationHelper(request: ScanHelperRequest) = apiService.createLocationHelper(request)
}