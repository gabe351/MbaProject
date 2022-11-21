package com.gabia.mbaproject.infrastructure.remote.remotedatasource

import com.gabia.mbaproject.infrastructure.utils.BaseCallBack
import com.gabia.mbaproject.model.AuthRequest
import com.gabia.mbaproject.model.User

interface AuthRemoteDataSourceContract {
    fun login(authRequest: AuthRequest, resultCallBack: BaseCallBack<User?>)
    fun changePassword(authRequest: AuthRequest, resultCallBack: BaseCallBack<Int>)
}