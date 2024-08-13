package com.example.imagereader.repository

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.imagereader.dao.ImageDao
import com.example.imagereader.entity.ImageEntity
import java.io.File

class ImageRepository(private val imageDao: ImageDao) {

    fun getAllImagesFromGallery(context: Context): List<String> {
        val imagesList = mutableListOf<String>()
        val projection = arrayOf(Media.DATA)
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            //val columnIndex = cursor.getColumnIndexOrThrow(Media.DATA)
            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(0)
                imagesList.add(imagePath)
            }


        }
        return imagesList
    }

    suspend fun getAllImagesFromDatabase(): List<ImageEntity> {
        return imageDao.getAllImages()
    }

    suspend fun storeAllImagesInDatabase(images: List<String>) {
        images.forEach { image ->
            imageDao.insertImage(ImageEntity(0, image))
        }
    }

    suspend fun deleteImageFromDatabase(imageId: Int) {
        imageDao.deleteImage(imageId)


    }

    suspend fun deleteImageFromSystem(imagePath: String): Boolean {
        val file = File(imagePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }

    }

    suspend fun renameImage(imageEntity: ImageEntity, newName: String): Boolean {
        val oldFile = File(imageEntity.imagePath)
        val newFile = File(oldFile.parentFile, "$newName.${oldFile.extension}")

        Log.d("ImageRepository", "Old file path: ${oldFile.absolutePath}")
        Log.d("ImageRepository", "New file path: ${newFile.absolutePath}")
        Log.d("ImageRepository", "Old file exists: ${oldFile.exists()}")

        return try {
            if (oldFile.exists()) {

                val renamed = oldFile.renameTo(newFile)
                Log.d("Image", "renameImage: $renamed")
                if (renamed) {
                    val updatedImageEntity = imageEntity.copy(imagePath = newFile.absolutePath)
                    imageDao.updateImage(updatedImageEntity)
                    Log.e("ImageRepository", "Renamed")

                }
                renamed
            } else {
                Log.e("ImageRepository", "Old file does not exist: ${oldFile.absolutePath}")
                false
            }
        } catch (e: Exception) {
            Log.e("ImageRepository", "Error renaming file: ${e.message}", e)
            false
        }
    }


//    fun renameImageUsingMediaStore(
//        context: Context,
//        imageEntity: ImageEntity,
//        newName: String
//    ): Boolean {
//        val oldFile = File(imageEntity.imagePath)
//        val newFile = File(oldFile.parentFile, "$newName.${oldFile.extension}")
//        val newFileName = "$newName.${oldFile.extension}"
//
//        Log.d("ImageRepository", "Old file path: ${oldFile.absolutePath}")
//        Log.d("ImageRepository", "New file path: ${newFile.absolutePath}")
//        Log.d("ImageRepository", "Old file exists: ${oldFile.exists()}")
//
//        return try {
//            if (oldFile.exists()) {
//
//                val renamed = oldFile.renameTo(newFile)
//                Log.d("Image", "renameImage: $renamed")
//                if (renamed) {
//                    val updatedImageEntity = imageEntity.copy(imagePath = newFile.absolutePath)
//                    imageDao.updateImage(updatedImageEntity)
//                    Log.e("ImageRepository", "Renamed")
//                }
//                renamed
//            } else {
//                Log.e("ImageRepository", "Old file does not exist: ${oldFile.absolutePath}")
//                false
//            }
//        } catch (e: Exception) {
//            Log.e("ImageRepository", "Error renaming file: ${e.message}", e)
//            false
//        }
//    }
}











//  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            val fromUri = Uri.withAppendedPath(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                imageEntity.imageId.toString()
//            )
//
//            val contentValues = ContentValues().apply {
//                put(MediaStore.Images.Media.DISPLAY_NAME, newFileName)
//            }
//           try {
//                // Try to update MediaStore with new name
//                val updatedRows = context.contentResolver.update(fromUri, contentValues, null, null)
//                if (updatedRows > 0) {
//                    Log.d("ImageRepository", "MediaStore updated successfully")
//
//                    // Update the Room database with the new file path
//                    val updatedFile = File(oldFile.parent, newFileName)
//                    val updatedImageEntity = imageEntity.copy(imagePath = updatedFile.absolutePath)
//                    imageDao.updateImage(updatedImageEntity)
//
//                    Log.d("ImageRepository", "Room database updated with new path")
//                    return true
//                } else {
//                    Log.e("ImageRepository", "Failed to update MediaStore")
//                }
//            } catch (e: SecurityException) {
//                Log.e("ImageRepository", "SecurityException: ${e.message}", e)
//                // Handle permission errors or access issues here
//            }



//        if (ContextCompat.checkSelfPermission(
//                context,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.e("ImageRepository", "WRITE_EXTERNAL_STORAGE permission not granted")
//            return false
//        }


//val fromUri = Uri.withAppendedPath(
//    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//    imageEntity.imageId.toString()
//)
//ContentValues().also {
//    it.put(MediaStore.Files.FileColumns.IS_PENDING, 1)
//    context.contentResolver.update(fromUri, it, null, null)
//    it.clear()
//
//    //updating file details
//    it.put(MediaStore.Files.FileColumns.DISPLAY_NAME, newName)
//    it.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
//    val updatedRows = context.contentResolver.update(fromUri, it, null, null)
//
//    if (updatedRows > 0) {
//        Log.d("ImageRepository", "MediaStore updated successfully")
//
//        // Update the Room database with the new file path
//        val updatedFile = File(oldFile.parent, newFileName)
//        val updatedImageEntity = imageEntity.copy(imagePath = updatedFile.absolutePath)
//        imageDao.updateImage(updatedImageEntity)
//
//        Log.d("ImageRepository", "Room database updated with new path")
//        isRenamed = true
//    } else {
//        Log.e("ImageRepository", "Failed to update MediaStore")
//        isRenamed = false
//    }
//
//}





//            val contentResolver = context.contentResolver
//            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            val selection = "${MediaStore.Images.Media.DATA} = ?"
//            val selectionArgs = arrayOf(imageEntity.imagePath)
//
//            // Prepare ContentValues with new name
//            val contentValues = ContentValues().apply {
//                put(MediaStore.Images.Media.DISPLAY_NAME, newFileName)
//            }
//
//            // Update MediaStore
//            val rowsUpdated = contentResolver.update(uri, contentValues, selection, selectionArgs)
//
//            if (rowsUpdated > 0) {
//                Log.d("ImageRepository", "MediaStore updated successfully")
//
//                // Update the Room database with the new file path
//                val newFile = File(oldFile.parent, newFileName)
//                val updatedImageEntity = imageEntity.copy(imagePath = newFile.absolutePath)
//                imageDao.updateImage(updatedImageEntity)
//
//                Log.d("ImageRepository", "Room database updated with new path")
//                return true
//            } else {
//                Log.e("ImageRepository", "Failed to update MediaStore")
//                return false
//            }