package io.github.king0929zion.zionchatz.di

import io.github.king0929zion.zionchatz.data.files.FilesManager
import io.github.king0929zion.zionchatz.data.repository.ConversationRepository
import io.github.king0929zion.zionchatz.data.repository.FavoriteRepository
import io.github.king0929zion.zionchatz.data.repository.FilesRepository
import io.github.king0929zion.zionchatz.data.repository.GenMediaRepository
import io.github.king0929zion.zionchatz.data.repository.MemoryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single {
        ConversationRepository(get(), get(), get(), get(), get(), get())
    }

    single {
        MemoryRepository(get())
    }

    single {
        GenMediaRepository(get())
    }

    single {
        FilesRepository(get())
    }

    single {
        FavoriteRepository(get())
    }

    single {
        FilesManager(get(), get(), get())
    }
}
