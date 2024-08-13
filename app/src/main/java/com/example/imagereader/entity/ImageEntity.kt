package com.example.imagereader.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val imageId:Int,
    @ColumnInfo(name="image_path") val imagePath:String
)
