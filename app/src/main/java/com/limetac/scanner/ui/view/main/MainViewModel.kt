package com.limetac.scanner.ui.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.limetac.scanner.data.model.User
import com.limetac.scanner.data.repository.MainRepository
import com.limetac.scanner.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val users = MutableLiveData<Resource<List<User>>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        users.postValue(Resource.loading(null))
        compositeDisposable.add(
            mainRepository.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userList ->
                    users.postValue(Resource.success(userList))
                }, { throwable ->
                    users.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getUsers(): LiveData<Resource<List<User>>> {
        return users
    }

}