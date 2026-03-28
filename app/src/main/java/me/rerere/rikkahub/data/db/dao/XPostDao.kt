package me.rerere.rikkahub.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.XPostEntity

@Dao
interface XPostDao {
    @Query("SELECT * FROM x_post_entity WHERE parent_post_id IS NULL ORDER BY create_at DESC")
    fun observeFeed(): Flow<List<XPostEntity>>

    @Query("SELECT * FROM x_post_entity WHERE id = :id")
    fun observePost(id: String): Flow<XPostEntity?>

    @Query("SELECT * FROM x_post_entity WHERE parent_post_id = :parentId ORDER BY create_at ASC")
    fun observeReplies(parentId: String): Flow<List<XPostEntity>>

    @Query("SELECT * FROM x_post_entity WHERE id = :id")
    suspend fun getById(id: String): XPostEntity?

    @Query("SELECT * FROM x_post_entity WHERE author_kind = 'bot' ORDER BY create_at DESC LIMIT 1")
    suspend fun getLatestBotPost(): XPostEntity?

    @Query("SELECT * FROM x_post_entity WHERE assistant_id = :assistantId ORDER BY create_at DESC LIMIT 1")
    suspend fun getLatestPostByAssistant(assistantId: String): XPostEntity?

    @Query("SELECT COUNT(*) FROM x_post_entity")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM x_post_entity WHERE parent_post_id = :parentId AND assistant_id = :assistantId")
    suspend fun countRepliesByAssistant(parentId: String, assistantId: String): Int

    @Query("SELECT * FROM x_post_entity WHERE parent_post_id IS NULL ORDER BY create_at DESC LIMIT :limit")
    suspend fun getRecentFeed(limit: Int): List<XPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(post: XPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<XPostEntity>)
}
