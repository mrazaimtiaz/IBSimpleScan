package com.integratedbiometrics.ibsimplescan

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.integratedbiometrics.ibsimplescan.utils.UserData

//@Database(entities = [UserData::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun userDataDao(): UserDataDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "app_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            ).build()
    }
}