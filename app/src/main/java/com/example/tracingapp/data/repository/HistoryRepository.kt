package com.example.tracingapp.data.repository

import com.example.tracingapp.data.dao.HistoryDao
import com.example.tracingapp.data.model.History
import com.example.tracingapp.data.model.User
import kotlinx.coroutines.flow.Flow

// emit multiple values
// suspends return single value

class HistoryRepository(private val dao: HistoryDao) {
    // insert
    fun checkInUser(history: History) {
        dao.checkInUser(history)
    }

    // view own
    fun getOwnHistory(userId: Int): Flow<List<History>> {
        return dao.getHistory(userId)
    }
}