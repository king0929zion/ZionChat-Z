package me.rerere.rikkahub.data.repository

import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.dao.XPostDao
import me.rerere.rikkahub.data.db.entity.XPostEntity

class XPostRepository(private val dao: XPostDao) {
    fun observeFeed(): Flow<List<XPostEntity>> = dao.observeFeed()

    fun observePost(id: String): Flow<XPostEntity?> = dao.observePost(id)

    fun observeReplies(parentId: String): Flow<List<XPostEntity>> = dao.observeReplies(parentId)

    suspend fun getById(id: String): XPostEntity? = dao.getById(id)

    suspend fun getLatestBotPost(): XPostEntity? = dao.getLatestBotPost()

    suspend fun getLatestPostByAssistant(assistantId: String): XPostEntity? = dao.getLatestPostByAssistant(assistantId)

    suspend fun countAll(): Int = dao.countAll()

    suspend fun countRepliesByAssistant(parentId: String, assistantId: String): Int = dao.countRepliesByAssistant(parentId, assistantId)

    suspend fun getRecentFeed(limit: Int = 8): List<XPostEntity> = dao.getRecentFeed(limit)

    suspend fun upsert(post: XPostEntity) = dao.upsert(post)

    suspend fun insertAll(posts: List<XPostEntity>) = dao.insertAll(posts)
}
