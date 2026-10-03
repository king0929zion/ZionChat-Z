package me.rerere.rikkahub.data.db

import android.content.Context
import android.os.Build
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import io.requery.android.database.sqlite.SQLiteCustomExtension
import io.requery.android.database.sqlite.SQLiteDatabaseConfiguration
import java.io.File
import java.util.zip.ZipFile

/** Use the same SQLite extensions for the live database and backup validation. */
internal object SQLiteConfiguration {
    const val DATABASE_NAME = "rikka_hub"

    fun configure(context: Context, configuration: SQLiteDatabaseConfiguration): SQLiteDatabaseConfiguration {
        configuration.customExtensions.add(
            SQLiteCustomExtension(resolveSqliteExtensionPath(context, "libsimple.so"), null)
        )
        return configuration
    }

    fun openHelperFactory(context: Context) = RequerySQLiteOpenHelperFactory(
        listOf(RequerySQLiteOpenHelperFactory.ConfigurationOptions { configure(context, it) })
    )

    /**
     * 优先使用 nativeLibraryDir 中的扩展库; 部分设备上 nativeLibraryDir 未正确释放,
     * 此时回退到从 APK 内解压到 noBackupFilesDir (fork 时期的加载修复, 保留)。
     */
    private fun resolveSqliteExtensionPath(context: Context, libraryName: String): String {
        return resolveSqliteExtensionFile(context, libraryName)
            .absolutePath
            .removeSuffix(".so")
    }

    private fun resolveSqliteExtensionFile(context: Context, libraryName: String): File {
        val nativeLibrary = File(context.applicationInfo.nativeLibraryDir, libraryName)
        if (nativeLibrary.exists()) {
            return nativeLibrary
        }

        val sourceApk = File(context.applicationInfo.sourceDir)
        val extractionDir = File(context.noBackupFilesDir, "sqlite_extensions").apply {
            mkdirs()
        }
        val extractedLibrary = File(extractionDir, libraryName)
        if (extractedLibrary.exists() && extractedLibrary.lastModified() >= sourceApk.lastModified()) {
            return extractedLibrary
        }

        ZipFile(sourceApk).use { apk ->
            val entry = Build.SUPPORTED_ABIS
                .asSequence()
                .mapNotNull { abi -> apk.getEntry("lib/$abi/$libraryName") }
                .firstOrNull()
                ?: error("Could not find $libraryName in ${sourceApk.absolutePath}")

            val tempFile = File(extractionDir, "$libraryName.tmp")
            apk.getInputStream(entry).use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (extractedLibrary.exists()) {
                extractedLibrary.delete()
            }
            tempFile.renameTo(extractedLibrary)
            extractedLibrary.setLastModified(sourceApk.lastModified())
        }

        return extractedLibrary
    }
}
