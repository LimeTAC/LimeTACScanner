package com.limetac.scanner.ui.view.bin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.limetac.scanner.data.api.request.BinRequest
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.data.model.EntityType
import com.limetac.scanner.data.repository.BinRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BinViewModel(private val repository: BinRepository) : ViewModel() {

    private val binDetails = MutableLiveData<Resource<BinResponse>>()
    private val entityDetails = MutableLiveData<Resource<BinResponse>>()
    private val compositeDisposable = CompositeDisposable()


    fun submitBin(binCode: String, tags: List<BinTag>) {
        val request = BinRequest()
        request.tagList = tags
        request.locationCode = binCode
        binDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.submitBin(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    binDetails.postValue(Resource.success(pkg))
                }, {
                    binDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getEntity(code: String) {
        val request = EntityTagRequest()
        request.entityType = EntityType.BIN.type
        request.code = code
        entityDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getEntityTags(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    entityDetails.postValue(Resource.success(pkg))
                }, {
                    entityDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    public fun releaseRequest(binCode: String, tags: List<BinTag>) {
        val request = BinRequest()
        request.tagList = tags
        request.locationCode = binCode
        binDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.submitBin(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    binDetails.postValue(Resource.success(pkg))
                }, {
                    binDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getBinDetails(): LiveData<Resource<BinResponse>> {
        return binDetails;
    }

    fun getEntityDetails(): LiveData<Resource<BinResponse>> {
        return entityDetails;
    }

}