package io.github.king0929zion.zionchatz.data.repository

import androidx.paging.PagingSource
import io.github.king0929zion.zionchatz.data.db.dao.GenMediaDAO
import io.github.king0929zion.zionchatz.data.db.entity.GenMediaEntity

class GenMediaRepository(private val dao: GenMediaDAO) {
    fun getAllMedia(): PagingSource<Int, GenMediaEntity> = dao.getAll()

    suspend fun insertMedia(media: GenMediaEntity) = dao.insert(media)

    suspend fun deleteMedia(id: Int) = dao.delete(id)
}
