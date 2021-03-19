package com.example.hilt

import com.example.hilt.db.User
import com.example.hilt.db.UserDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val userDao: UserDao
){

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getAllUsers() = userDao.getAllUsers()

}