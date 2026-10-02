package io.github.king0929zion.zchat.di

import io.github.king0929zion.zchat.data.files.FilesManager
import io.github.king0929zion.zchat.data.repository.ConversationRepository
import io.github.king0929zion.zchat.data.repository.FavoriteRepository
import io.github.king0929zion.zchat.data.repository.FilesRepository
import io.github.king0929zion.zchat.data.repository.GenMediaRepository
import io.github.king0929zion.zchat.data.repository.MemoryRepository
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
