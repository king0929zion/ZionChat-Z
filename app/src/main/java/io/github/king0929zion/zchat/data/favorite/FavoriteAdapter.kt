package io.github.king0929zion.zchat.data.favorite

import io.github.king0929zion.zchat.data.db.entity.FavoriteEntity
import io.github.king0929zion.zchat.data.model.FavoriteType

interface FavoriteAdapter<T> {
    val type: FavoriteType

    fun buildRefKey(target: T): String

    fun buildFavoriteEntity(
        target: T,
        existing: FavoriteEntity? = null,
        now: Long = System.currentTimeMillis()
    ): FavoriteEntity
}
