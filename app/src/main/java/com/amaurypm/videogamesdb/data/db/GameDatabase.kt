package com.amaurypm.videogamesdb.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amaurypm.videogamesdb.data.db.model.GameEntity
import com.amaurypm.videogamesdb.util.Constants

@Database(
    entities = [GameEntity::class],
    version = 1, //versión de la bd para migraciones
    exportSchema = true //por defecto es true.
)
abstract class GameDatabase : RoomDatabase() {
    //Aquí va el DAO
    abstract fun gameDao(): GameDao

    //Sin inyección de dependencias, instanciamos la base de datos
    //aquí con un patrón singleton

    companion object {

        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            //Si la instancia no es nula, entonces vamos
            //a regresar la que ya tenemos
            //Si es nula, creamos una instancia y la regresamos
            //(patrón singleton)

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }

        }

    }

}