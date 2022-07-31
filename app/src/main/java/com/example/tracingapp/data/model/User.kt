package com.example.tracingapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
class User (
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0,

    @ColumnInfo(name = "fullname")
    var fullname: String = "",

    @ColumnInfo(name = "password")
    var password: String = "",

    @ColumnInfo(name = "phone")
    var phone: String = "",

    @ColumnInfo(name = "ic/passport")
    var ic: String = "XXXXXX-XX-XXXX",

    @ColumnInfo(name = "location")
    var location: String = "",

    @ColumnInfo(name = "vaccine")
    var vaccine: String = "None",

    @ColumnInfo(name = "isVerified")
    var isVerified: Boolean = false,

    @ColumnInfo(name = "isActive")
    var isActive: Boolean = false
)