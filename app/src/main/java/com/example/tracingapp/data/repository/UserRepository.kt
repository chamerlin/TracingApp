package com.example.tracingapp.data.repository

import com.example.tracingapp.data.dao.UserDao
import com.example.tracingapp.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {
    // register
    fun registerUser(user: User) {
        dao.registerUser(user)
    }

    // update user
    fun updateUser(user: User) {
        dao.updateUser(user)
    }

    // login
    suspend fun loginUser(phone: String, password: String): User {
        dao.isLogin(true, phone)
        return dao.loginUser(phone, password)
    }

    fun logoutUser() {
        dao.logoutUser()
    }

    // login user details
    val loginUserDetails: Flow<User> = dao.loginUserDetails()

    suspend fun findUser(phone: String): User {
        return dao.findUser(phone)
    }
}