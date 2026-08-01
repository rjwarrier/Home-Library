package com.mj.homelibrary

import android.app.Application
import com.mj.homelibrary.data.AppDatabase
import com.mj.homelibrary.data.BackupRepository
import com.mj.homelibrary.data.CoverCache
import com.mj.homelibrary.data.HomeLibraryRepository
import com.mj.homelibrary.data.remote.BookLookupService

class HomeLibraryApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.create(this) }

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
}
