package me.rerere.rikkahub.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 桥接迁移: 19 -> 20
 *
 * 本仓库历史上基于上游 v16 schema 独立演进到 v19 (v19 实际等价于上游 v16 结构:
 * ConversationEntity 仍保留 truncate_index, GenMediaEntity 缺少 type/source_paths,
 * 且缺少 custom_system_prompt)。上游则在 v17-v20 期间完成了这些列变更。
 *
 * 该迁移用 PRAGMA 防御式补齐差异, 使得两种 v19 结构 (fork 遗留 / 上游) 都能安全迁移:
 * 1. 删除 ConversationEntity.truncate_index (上游 v16 -> v17 的变更)
 * 2. 补齐 GenMediaEntity.type / source_paths (上游 v17 -> v18 的变更)
 * 3. 补齐 ConversationEntity.custom_system_prompt (上游 v18 -> v19 的变更)
 * 4. 补齐 ConversationEntity.mode_injection_ids / lorebook_ids (上游 v19 -> v20 的变更)
 */
val Migration_19_20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. drop legacy truncate_index column if it still exists (fork legacy v19)
        if (hasColumn(db, "ConversationEntity", "truncate_index")) {
            db.execSQL("ALTER TABLE `ConversationEntity` DROP COLUMN `truncate_index`")
        }

        // 2. GenMediaEntity new columns (upstream v17 -> v18)
        if (!hasColumn(db, "GenMediaEntity", "type")) {
            db.execSQL(
                "ALTER TABLE `GenMediaEntity` ADD COLUMN `type` TEXT NOT NULL DEFAULT 'image_generation'"
            )
        }
        if (!hasColumn(db, "GenMediaEntity", "source_paths")) {
            db.execSQL("ALTER TABLE `GenMediaEntity` ADD COLUMN `source_paths` TEXT")
        }

        // 3. ConversationEntity.custom_system_prompt (upstream v18 -> v19)
        if (!hasColumn(db, "ConversationEntity", "custom_system_prompt")) {
            db.execSQL(
                "ALTER TABLE `ConversationEntity` ADD COLUMN `custom_system_prompt` TEXT NOT NULL DEFAULT ''"
            )
        }

        // 4. ConversationEntity prompt injection columns (upstream v19 -> v20)
        if (!hasColumn(db, "ConversationEntity", "mode_injection_ids")) {
            db.execSQL(
                "ALTER TABLE `ConversationEntity` ADD COLUMN `mode_injection_ids` TEXT NOT NULL DEFAULT '[]'"
            )
        }
        if (!hasColumn(db, "ConversationEntity", "lorebook_ids")) {
            db.execSQL(
                "ALTER TABLE `ConversationEntity` ADD COLUMN `lorebook_ids` TEXT NOT NULL DEFAULT '[]'"
            )
        }
    }

    private fun hasColumn(db: SupportSQLiteDatabase, table: String, column: String): Boolean {
        db.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == column) return true
            }
        }
        return false
    }
}
