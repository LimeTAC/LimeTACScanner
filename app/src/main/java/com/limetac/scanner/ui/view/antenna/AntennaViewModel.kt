package com.limetac.scanner.ui.view.antenna

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.api.request.ForkliftRequest
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.data.model.EntityType
import com.limetac.scanner.data.repository.AntennaRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AntennaViewModel(private val repository: AntennaRepository) : ViewModel() {

    private val details = MutableLiveData<Resource<BinResponse>>()
    private val entityDetails = MutableLiveData<Resource<BinResponse>>()
    private val verifyTag = MutableLiveData<Resource<BinResponse>>()
    private val compositeDisposable = CompositeDisposable()


    fun submitAntenna(binCode: String, tags: List<BinTag>) {
        val request = ForkliftRequest()
        val validTags = ArrayList<BinTag>()
        for (tag in tags) {
            if (tag.tagCode.isNotEmpty())
                validTags.add(tag)
        }
        request.tagList = validTags
        request.forkliftCode = binCode
        details.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.submitForklift(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    details.postValue(Resource.success(pkg))
                }, {
                    details.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }


    fun getEntity(code: String) {
        val request = EntityTagRequest()
        request.entityType = EntityType.ANTENNA.type
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

    fun verifyTag(code: String) {
        val request = EntityTagRequest()
      //  request.entityType = EntityType.ANTENNA.type
        request.code = code
        verifyTag.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getEntityTags(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    verifyTag.postValue(Resource.success(pkg))
                }, {
                    verifyTag.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getDetails(): LiveData<Resource<BinResponse>> {
        return details;
    }

    fun getEntityDetails(): LiveData<Resource<BinResponse>> {
        return entityDetails;
    }

    fun getVerifyTag(): LiveData<Resource<BinResponse>> {
        return verifyTag;
    }

}