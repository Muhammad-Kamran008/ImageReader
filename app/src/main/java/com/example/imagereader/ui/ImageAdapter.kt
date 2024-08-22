package com.example.imagereader.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagereader.ImageDetailActivity
import com.example.imagereader.R
import com.example.imagereader.databinding.ImageItemBinding
import com.example.imagereader.entity.ImageEntity
import com.example.imagereader.repository.ImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageAdapter(private val imageEntities: List<ImageEntity>,
                   private val imageRepository: ImageRepository,
                   private val context: Context
) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageEntities.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentImageEntity = imageEntities[position]
        val context = holder.itemView.context

        Glide.with(context)
            .load(currentImageEntity.imagePath)
            .into(holder.binding.imageView)

        holder.binding.menuButton.setOnClickListener { view ->
            showPopupMenu(view, position)
        }

        holder.binding.imageView.setOnClickListener {
            val intent = Intent(context, ImageDetailActivity::class.java).apply {
                putExtra("IMAGE_PATH", currentImageEntity.imagePath)
                // putExtra("IMAGE_NAME", currentImageEntity.imageName)
                putExtra("IMAGE_ID", currentImageEntity.imageId)
            }
            context.startActivity(intent)
        }
    }

    private fun showPopupMenu(view: View, position: Int) {

        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_item)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    handleEditAction(position)
                    true
                }
                R.id.action_delete -> {
                    handleDeleteAction(position)
                    true
                }

                R.id.action_send->{
                    handleSendAction(position)
                    true
                }
                else -> false
            }
        }


        popupMenu.show()
    }

    private fun handleEditAction(position: Int) {
        val currentImageEntity = imageEntities[position]
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rename_image, null)
        val editTextNewName = dialogView.findViewById<EditText>(R.id.editTextNewName)


        AlertDialog.Builder(context).apply {
            setTitle("Rename Image")
            setView(dialogView)
            setPositiveButton("Rename") { dialog, _ ->
                val newName = editTextNewName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    renameImage(currentImageEntity, newName, position)
                } else {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        }.create().show()
    }

    private fun renameImage(imageEntity: ImageEntity, newName: String, position: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            val isRenamed = imageRepository.renameImage(imageEntity, newName)
            withContext(Dispatchers.Main) {
                if (isRenamed) {
                    Toast.makeText(context, "Image renamed", Toast.LENGTH_SHORT).show()
                    val updatedImageEntity = imageEntity.copy(imagePath = File(imageEntity.imagePath).parentFile?.resolve("$newName.${File(imageEntity.imagePath).extension}")?.absolutePath ?: "")
                    (imageEntities as MutableList)[position] = updatedImageEntity
                    notifyItemChanged(position)
                } else {
                    Toast.makeText(context, "Failed to rename image", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    private fun handleDeleteAction(position: Int) {
        val currentImageEntity=imageEntities[position]
        CoroutineScope(Dispatchers.IO).launch {
            imageRepository.deleteImageFromDatabase(currentImageEntity.imageId)
            imageRepository.deleteImageFromSystem(currentImageEntity.imagePath)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                // Remove the deleted item from the list and notify the adapter
                (imageEntities as MutableList).removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun handleSendAction(position: Int) {
        val currentImageEntity = imageEntities[position]
        val imageFile = File(currentImageEntity.imagePath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}

