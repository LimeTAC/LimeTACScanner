package com.limetac.scanner.data.api

import com.limetac.scanner.BuildConfig
import com.limetac.scanner.data.api.request.*
import com.limetac.scanner.data.model.PackagingItem
import com.limetac.scanner.data.model.PkgDetails
import com.limetac.scanner.data.model.User
import com.limetac.scanner.utils.Constants.Environment.CURRENT_ENVIRONMENT
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject

class ApiServiceImpl : ApiService {

    override fun getUsers(): Single<List<User>> {
        return Rx2AndroidNetworking.get(CURRENT_ENVIRONMENT + "https://5e510330f2c0d300147c034c.mockapi.io/users")
            .build()
            .getObjectListSingle(User::class.java)
    }

    override fun getTagsByPkg(pkgRequest: PkgRequest): Single<PkgDetails> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/GetPackageTagsByPackageCode")
            .addApplicationJsonBody(pkgRequest)
            .build()
            .getObjectSingle(PkgDetails::class.java)
    }

    override fun releasePkgTag(request: PkgRequest): Single<JSONObject> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/ReleasePackageTag")
            .addApplicationJsonBody(request)
            .build()
            .getJSONObjectSingle();
    }

    override fun getTagsByTags(request: PkgRequest): Single<PkgDetails> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/GetPackageTagsByTagCode")
            .addApplicationJsonBody(request)
            .build()
            .getObjectSingle(PkgDetails::class.java)
    }

    override fun submitPkg(request: AddPkgRequest): Single<PkgDetails> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/CreatePackageTags")
            .addApplicationJsonBody(request)
            .build()
            .getObjectSingle(PkgDetails::class.java)
    }

    override fun getPackagingItems(request: PkgRequest): Observable<List<PackagingItem>> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/GetPackingItems")
            .addApplicationJsonBody(request)
            .build()
            .getObjectListObservable(PackagingItem::class.java)
    }

    override fun submitBin(request: BinRequest): Single<BinResponse> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/CreateDoubleReadBinTags")
            .addApplicationJsonBody(request)
            .build()
            .getObjectSingle(BinResponse::class.java)
    }

    override fun releaseTag(request: ReleaseRequest): Single<JSONObject> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/ReleaseEntityTag")
            .addApplicationJsonBody(request)
            .build()
            .getJSONObjectSingle();
    }

    override fun getEntityTag(request: EntityTagRequest): Single<BinResponse> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/GetEntityTagsByCode")
            .addApplicationJsonBody(request)
            .build()
            .getObjectSingle(BinResponse::class.java)
    }

    override fun submitForkLift(request: ForkliftRequest): Single<BinResponse> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/CreateForkliftAntennaTags")
            .addApplicationJsonBody(request)
            .build()
            .getObjectSingle(BinResponse::class.java)
    }

    override fun getEntityByTag(request: EntityTagRequest): Single<List<BinResponse>> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/GetAllEntitiesByTagCode")
            .addApplicationJsonBody(request)
            .build()
            .getObjectListSingle(BinResponse::class.java)
    }

    override fun createLocationHelper(request: BinRequest): Single<BinResponse> {
        return Rx2AndroidNetworking.post("$CURRENT_ENVIRONMENT/api/inventory/CreateHelperLocation/")
            .addApplicationJsonBody(request)
            .build()
            .getObjectSingle(BinResponse::class.java)
    }
}