package io.github.king0929zion.zionchatz.web.routes

import kotlin.uuid.Uuid
import io.github.king0929zion.zionchatz.web.BadRequestException

internal fun String?.toUuid(name: String = "id"): Uuid {
    if (this == null) throw BadRequestException("Missing $name")
    return runCatching { Uuid.parse(this) }.getOrNull()
        ?: throw BadRequestException("Invalid $name")
}
