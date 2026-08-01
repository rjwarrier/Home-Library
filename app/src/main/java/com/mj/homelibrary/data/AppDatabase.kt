package com.mj.homelibrary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mj.homelibrary.data.dao.BookDao
import com.mj.homelibrary.data.dao.LoanDao
import com.mj.homelibrary.data.dao.LocationDao
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity

@Database(
    entities = [BookEntity::class, LocationEntity::class, LoanEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun locationDao(): LocationDao
    abstract fun loanDao(): LoanDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN purchaseDateEpochMillis INTEGER")
                db.execSQL("ALTER TABLE books ADD COLUMN cost REAL")
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "home-library.db")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration(false)
                .build()
    }
}
