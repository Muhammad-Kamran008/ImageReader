package com.example.imagereader.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.imagereader.entity.ImageEntity

@Dao
interface ImageDao {
    @Query("SELECT * FROM images")
    fun getAllImages(): List<ImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(imageEntity: ImageEntity)

    @Query("DELETE FROM images WHERE imageId = :imageId")
    fun deleteImage(imageId: Int)

    @Update()
    fun updateImage(imageEntity: ImageEntity)

}

