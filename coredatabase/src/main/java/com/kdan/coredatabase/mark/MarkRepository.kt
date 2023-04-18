package com.kdan.coredatabase.mark

import javax.inject.Inject

class MarkRepository @Inject constructor(private val dao: MarkDao) {

    suspend fun upsertMark(mark: Mark) = dao.upsertMark(mark)

    suspend fun deleteMark(mark: Mark) = dao.deleteMark(mark)

    suspend fun getMarks(email: String): List<Mark> = dao.getMarks(email)
}