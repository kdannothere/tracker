package com.kdan.coredatabase.user

import javax.inject.Inject

class UserRepository @Inject constructor(private val dao: UserDao) {

    suspend fun upsertUser(user: User) = dao.upsertUser(user)

    suspend fun getUser(): User? = dao.getUser()
}