package com.example.tracingapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tracingapp.data.model.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    // insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun checkInUser(history: History)

    // view
    @Query("SELECT * FROM history_table WHERE userId = :userId")
    fun getHistory(userId: Int): Flow<List<History>>
}