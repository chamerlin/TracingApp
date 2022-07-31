package com.example.tracingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tracingapp.data.dao.HistoryDao
import com.example.tracingapp.data.dao.UserDao
import com.example.tracingapp.data.model.History
import com.example.tracingapp.data.model.User

@Database(
    entities = [
        User::class,
        History::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TracingDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: TracingDatabase? = null

        fun getInstance(context: Context): TracingDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TracingDatabase::class.java,
                        "tracing_db"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}