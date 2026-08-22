package com.mj.homelibrary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mj.homelibrary.data.dao.BookDao
import com.mj.homelibrary.data.dao.BorrowerDao
import com.mj.homelibrary.data.dao.LoanDao
import com.mj.homelibrary.data.dao.LocationDao
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.BorrowerEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity

@Database(
    entities = [BookEntity::class, LocationEntity::class, LoanEntity::class, BorrowerEntity::class],
    version = 6,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun locationDao(): LocationDao
    abstract fun loanDao(): LoanDao
    abstract fun borrowerDao(): BorrowerDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN purchaseDateEpochMillis INTEGER")
                db.execSQL("ALTER TABLE books ADD COLUMN cost REAL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN seriesName TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `borrowers` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`phone` TEXT, " +
                        "`relation` TEXT, " +
                        "`createdAtEpochMillis` INTEGER NOT NULL)",
                )
                db.execSQL("ALTER TABLE loans ADD COLUMN borrowerId INTEGER")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN mainGenre TEXT")
                db.execSQL("ALTER TABLE books ADD COLUMN subGenres TEXT NOT NULL DEFAULT '[]'")
                db.execSQL("ALTER TABLE books ADD COLUMN bookType TEXT")
                db.execSQL("ALTER TABLE books ADD COLUMN edition TEXT")
                db.execSQL("ALTER TABLE books ADD COLUMN signedCopy INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_books_readStatusCode` ON `books` (`readStatusCode`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_books_mainGenre` ON `books` (`mainGenre`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_books_addedDateEpochMillis` ON `books` (`addedDateEpochMillis`)")
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "home-library.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                .fallbackToDestructiveMigration(false)
                .build()
    }
}
