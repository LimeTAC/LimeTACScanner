package com.limetac.scanner.ui.view.scanHelper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.limetac.scanner.data.api.request.BinRequest
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.data.repository.ScanHelperRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ScanHelperViewModel(private val repository: ScanHelperRepository) : ViewModel() {

    private val scanHelperLiveData = MutableLiveData<Resource<BinResponse>>()
    private val entityDetails = MutableLiveData<Resource<BinResponse>>()
    private val compositeDisposable = CompositeDisposable()

    fun createScanHelper(scanHelperCode: String, tags: List<BinTag>) {
        val request = BinRequest()
        request.tagList = tags
        request.locationCode = scanHelperCode
        scanHelperLiveData.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.createScanHelper(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    scanHelperLiveData.postValue(Resource.success(it))
                }, {
                    scanHelperLiveData.postValue(Resource.error("Something Went Wrong", null))
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
}