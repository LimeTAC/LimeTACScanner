package com.limetac.scanner.ui.view.pkg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.limetac.scanner.data.api.request.AddPkgRequest
import com.limetac.scanner.data.api.request.PkgRequest
import com.limetac.scanner.data.model.PackagingItem
import com.limetac.scanner.data.model.PkgDetails
import com.limetac.scanner.data.model.Tag
import com.limetac.scanner.data.repository.PkgRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PackageViewModel(private val repository: PkgRepository) : ViewModel() {

    private val pkgDetails = MutableLiveData<Resource<PkgDetails>>()
    private val tagDetails = MutableLiveData<Resource<PkgDetails>>()
    private val submitPkg = MutableLiveData<Resource<PkgDetails>>()
    private val packagingItems = MutableLiveData<Resource<List<PackagingItem>>>()
    private val compositeDisposable = CompositeDisposable()


    fun fetchTagsByPkg(code: String) {
        val request = PkgRequest();
        request.packageCode = code
        pkgDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getTagsByPkg(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    pkgDetails.postValue(Resource.success(pkg))
                }, {
                    pkgDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun releaseTag(code: String) {
        val request = PkgRequest();
        request.tagCode = code
        compositeDisposable.add(
            repository.releaseTag(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->

                }, {
                    var error = it.message
                })
        )
    }

    fun getPackaging() {
        var request = PkgRequest();
        packagingItems.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getPackagingItem(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    packagingItems.postValue(Resource.success(pkg))
                }, {
                    packagingItems.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun submitPkg(pkgCode: String, tags: List<Tag>, selectedItem: String) {
        var request = AddPkgRequest();
        request.packageCode = pkgCode
        var tagList = ArrayList<String>()

        request.packingItemId = selectedItem
        for (tag in tags) {
            if (tag.tag.isNotEmpty())
                tagList.add(tag.tag)
        }
        request.tagCodeList = tagList

        pkgDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.submitPkg(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    submitPkg.postValue(Resource.success(pkg))
                }, {
                    submitPkg.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }


    fun fetchTagsByTags(tag: String) {
        var request = PkgRequest();
        request.tagCode = tag

        pkgDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getTagsByTag(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pkg ->
                    tagDetails.postValue(Resource.success(pkg))
                }, {
                    tagDetails.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPkgDetails(): LiveData<Resource<PkgDetails>> {
        return pkgDetails
    }

    fun getTagDetails(): LiveData<Resource<PkgDetails>> {
        return tagDetails
    }

    fun getPkgStatus(): LiveData<Resource<PkgDetails>> {
        return submitPkg
    }

    fun getPackagingItems(): LiveData<Resource<List<PackagingItem>>> {
        return packagingItems
    }
}