package com.example.tracingapp.data.relations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.example.tracingapp.data.model.History
import com.example.tracingapp.data.model.User

// not used
@Entity
data class UserHistory(
    @Embedded
    val user: User,

    @Relation(
        parentColumn = "userId",
        entityColumn = "historyId"
    )

    val history: List<History>
)