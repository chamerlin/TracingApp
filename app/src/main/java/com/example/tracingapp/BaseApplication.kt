package com.example.tracingapp

import android.app.Application
import com.example.tracingapp.data.TracingDatabase
import com.example.tracingapp.data.repository.HistoryRepository
import com.example.tracingapp.data.repository.UserRepository

class BaseApplication: Application() {
    val database: TracingDatabase by lazy { TracingDatabase.getInstance(this) }
    val userRepository: UserRepository by lazy { UserRepository(database.userDao()) }
    val historyRepository: HistoryRepository by lazy { HistoryRepository(database.historyDao()) }
}