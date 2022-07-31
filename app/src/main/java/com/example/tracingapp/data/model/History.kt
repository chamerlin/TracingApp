package com.example.tracingapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
class History (
    @PrimaryKey(autoGenerate = true)
    var historyId: Int = 0,

    @ColumnInfo(name = "location")
    var location: String = "",

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "time")
    var time: String = "",

    @ColumnInfo(name = "userId")
    var userId: Int = 0
)