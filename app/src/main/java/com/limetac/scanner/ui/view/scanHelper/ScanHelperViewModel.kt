package com.limetac.scanner.ui.view.scanHelper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.error.ANError
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.api.request.ReleaseTagRequest
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.data.model.ReleaseTagResponse
import com.limetac.scanner.data.model.ScanHelperRequest
import com.limetac.scanner.data.repository.ScanHelperRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

class ScanHelperViewModel(private val repository: ScanHelperRepository) : ViewModel() {

    private val scanHelperLiveData = MutableLiveData<Resource<BinResponse>>()
    private val entityDetails = MutableLiveData<Resource<BinResponse>>()
    private val releaseDetails = MutableLiveData<Resource<ReleaseTagResponse>>()
    private val compositeDisposable = CompositeDisposable()

    fun createScanHelper(scanHelperCode: String, tags: List<BinTag>) {
        /*      val request = BinRequest()
              request.tagList = tags
              request.locationCode = scanHelperCode*/
        val scanHelperRequest = ScanHelperRequest()
        scanHelperRequest.locationCode = scanHelperCode
        scanHelperRequest.tagCodes = ArrayList(tags.size)
        tags.forEachIndexed { index, element ->
            scanHelperRequest.tagCodes?.add(element.tagCode)
        }
        scanHelperLiveData.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.createScanHelper(scanHelperRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    scanHelperLiveData.postValue(Resource.success(it))
                }, {
                    scanHelperLiveData.postValue(Resource.error((it as ANError).errorBody, null))
                })
        )
    }

    fun releaseRequest(releaseTagRequest: ReleaseTagRequest) {
        compositeDisposable.add(
            repository.releaseTag(releaseTagRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    releaseDetails.postValue(Resource.success(it))
                }, {
                    releaseDetails.postValue(Resource.error(it.message.toString(), null))
                })
        )
    }

    fun getEntityDetail(code: String) {
        val request = EntityTagRequest()
        request.entityType = 1
        request.code = code
        entityDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getEntityDetail(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    entityDetails.postValue(Resource.success(pkg))
                }, {
                    entityDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getScanHelperLiveData(): LiveData<Resource<BinResponse>> {
        return scanHelperLiveData;
    }

    fun getEntityDetails(): LiveData<Resource<BinResponse>> {
        return entityDetails;
    }

    fun getReleaseLiveData(): LiveData<Resource<ReleaseTagResponse>> {
        return releaseDetails;
    }
}