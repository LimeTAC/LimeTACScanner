package com.limetac.scanner.ui.view.tag

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

class TagEntityViewModel(private val repository: EntityRepository) : ViewModel() {

    private val details = MutableLiveData<Resource<BinResponse>>()
    private val compositeDisposable = CompositeDisposable()


    fun getTagEntity(code: String) {
        val request = EntityTagRequest()
        request.tagCode = code
        details.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getEntityByTagCode(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    if (pkg.isNotEmpty())
                        details.postValue(Resource.success(pkg[0]))
                   else details.postValue(Resource.error("No entity is associated with this tag",null))
                }, {
                    details.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getDetails(): LiveData<Resource<BinResponse>> {
        return details;
    }
}