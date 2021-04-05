package com.limetac.scanner.ui.view.tagEntity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.EntityTagRequest
import com.limetac.scanner.data.repository.EntityRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TagScanningViewModel(private val repository: EntityRepository) : ViewModel() {

    private val tagDetails = MutableLiveData<Resource<List<BinResponse>>>()
    private val compositeDisposable = CompositeDisposable()

    fun getTagEntity(code: String) {
        val request = EntityTagRequest()
        request.tagCode = code
        tagDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getEntityByTagCode(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    if (pkg.isNotEmpty())
                        tagDetails.postValue(Resource.success(pkg))
                    else tagDetails.postValue(
                        Resource.error(
                            "No entity is associated with this tag",
                            null
                        )
                    )
                }, {
                    tagDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getTagDetails(): LiveData<Resource<List<BinResponse>>> {
        return tagDetails;
    }
}