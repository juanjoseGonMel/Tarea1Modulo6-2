package com.amaurypm.videogamesdb.data.db.model

import androidx.room.Entity
import com.amaurypm.videogamesdb.util.Constants

@Entity(tableName = Constants.DATABASE_GAME_TABLE)
data class GameEntity(
    var id: Long = 0,
    var title: String,
    var genre: String,
    var developer: String
)
