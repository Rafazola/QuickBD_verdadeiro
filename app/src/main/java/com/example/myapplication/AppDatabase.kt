package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Se mudar qualquer @Entity, aumente o version (ou desinstale o app do emulador),
// senão o Room acusa "Room cannot verify the data integrity".
@Database(entities = [TaskItemData::class, ItemLoja::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun itemLojaDao(): ItemLojaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val jaCriada = INSTANCE
                if (jaCriada != null) return jaCriada

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quick_database" // nome novo: não colide com o "app_database" do BDQuick
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
