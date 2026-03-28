package me.rerere.rikkahub.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "x_post_entity",
    indices = [
        Index(value = ["parent_post_id"]),
        Index(value = ["root_post_id"]),
        Index(value = ["assistant_id"]),
        Index(value = ["create_at"]),
    ]
)
data class XPostEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "parent_post_id")
    val parentPostId: String? = null,
    @ColumnInfo(name = "root_post_id")
    val rootPostId: String,
    @ColumnInfo(name = "assistant_id")
    val assistantId: String? = null,
    @ColumnInfo(name = "author_kind", defaultValue = "'user'")
    val authorKind: String = "user",
    @ColumnInfo(name = "author_name")
    val authorName: String,
    @ColumnInfo(name = "author_handle")
    val authorHandle: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "quote_author_name")
    val quoteAuthorName: String? = null,
    @ColumnInfo(name = "quote_handle")
    val quoteHandle: String? = null,
    @ColumnInfo(name = "quote_content")
    val quoteContent: String? = null,
    @ColumnInfo(name = "quote_preview")
    val quotePreview: String? = null,
    @ColumnInfo(name = "like_count", defaultValue = "0")
    val likeCount: Int = 0,
    @ColumnInfo(name = "reply_count", defaultValue = "0")
    val replyCount: Int = 0,
    @ColumnInfo(name = "repost_count", defaultValue = "0")
    val repostCount: Int = 0,
    @ColumnInfo(name = "view_count", defaultValue = "0")
    val viewCount: Int = 0,
    @ColumnInfo(name = "liked_by_user", defaultValue = "0")
    val likedByUser: Boolean = false,
    @ColumnInfo(name = "reposted_by_user", defaultValue = "0")
    val repostedByUser: Boolean = false,
    @ColumnInfo(name = "bookmarked_by_user", defaultValue = "0")
    val bookmarkedByUser: Boolean = false,
    @ColumnInfo(name = "create_at")
    val createAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_at")
    val updateAt: Long = System.currentTimeMillis(),
)
