package com.mj.homelibrary

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mj.homelibrary.data.AppDatabase
import com.mj.homelibrary.data.AppearanceSettingsRepository
import com.mj.homelibrary.data.BackupRepository
import com.mj.homelibrary.data.CoverCache
import com.mj.homelibrary.data.HomeLibraryRepository
import com.mj.homelibrary.data.LibrarySettingsRepository
import com.mj.homelibrary.data.remote.BookLookupService

class HomeLibraryApplication : Application(), ImageLoaderFactory {
    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val appearanceRepository: AppearanceSettingsRepository by lazy { AppearanceSettingsRepository(this) }
    val librarySettingsRepository: LibrarySettingsRepository by lazy { LibrarySettingsRepository(this) }

    val repository: HomeLibraryRepository by lazy {
        HomeLibraryRepository(
            bookDao = database.bookDao(),
            locationDao = database.locationDao(),
            loanDao = database.loanDao(),
            lookupService = BookLookupService(this),
            backupRepository = BackupRepository(this, database),
            coverCache = CoverCache(this),
        )
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .crossfade(200)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
}
