package com.kdan.coredatabase.markmap

import javax.inject.Inject

class MarkMapRepository @Inject constructor(private val dao: MarkMapDao) {

    suspend fun upsertMark(mark: MarkMap) = dao.upsertMark(mark)

    suspend fun deleteMark(mark: MarkMap) = dao.deleteMark(mark)

    suspend fun getMarks(email: String): List<MarkMap> = dao.getMarks(email)
}