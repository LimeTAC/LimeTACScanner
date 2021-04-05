package com.limetac.scanner.data.repository

import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.model.User
import io.reactivex.Single

class MainRepository(private val apiHelper: ApiHelper) {

    fun getUsers(): Single<List<User>> {
        return apiHelper.getUsers()
    }

}