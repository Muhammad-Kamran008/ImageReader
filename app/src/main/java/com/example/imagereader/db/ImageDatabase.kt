package com.example.imagereader.db

import android.content.Context
import android.media.Image
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imagereader.dao.ImageDao
import com.example.imagereader.entity.ImageEntity


//@Database(entities = [ImageEntity::class], version = 1)
//abstract class ImageDatabase:RoomDatabase() {
//    abstract fun imageDao():ImageDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: ImageDatabase? = null
//
//        fun getInstance(context: Context): ImageDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    ImageDatabase::class.java,
//                    "app_database"
//                ).allowMainThreadQueries().build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

@Database(entities = [ImageEntity::class], version = 1)
abstract class ImageDatabase:RoomDatabase(){
    abstract fun imageDao():ImageDao


    companion object{
        @Volatile
        private var INSTANCE:ImageDatabase?=null

        fun getDatabaseInstance(context: Context):ImageDatabase{
            return INSTANCE?: synchronized(this){
                val instance=Room.databaseBuilder(
                    context.applicationContext,
                    ImageDatabase::class.java,
                    "app_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

