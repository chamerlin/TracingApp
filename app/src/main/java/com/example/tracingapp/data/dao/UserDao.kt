package com.example.tracingapp.data.dao

import androidx.room.*
import com.example.tracingapp.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // register user
    @Insert
    fun registerUser(user: User)

    // update user
    @Update
    fun updateUser(user: User)

    // getCredentials
    @Query("SELECT * FROM user_table WHERE phone = :phone and password = :password")
    suspend fun loginUser(phone: String, password: String): User

    // set user active
    @Query("UPDATE user_table SET isActive = :isActive WHERE phone = :phone")
    suspend fun isLogin(isActive: Boolean, phone: String)

    // get login user details
    @Query("SELECT * FROM user_table WHERE isActive = 1")
    fun loginUserDetails(): Flow<User>

    // find user
    @Query("SELECT * FROM user_table WHERE phone = :phone")
    suspend fun findUser(phone: String): User

    // set all user to inactive
    @Query("UPDATE user_table SET isActive = 0 WHERE isActive = 1")
    fun logoutUser()
}