package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.CardPetImageItemBinding

class AdapterPetImageCard : RecyclerView.Adapter<AdapterPetImageCard.ViewHolder>() {
    lateinit var deleteImageListener: ((Int) -> Unit)
    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(
        this,
        diffUtil
    )

    var images: List<String>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class ViewHolder(val binding: CardPetImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onClickDeleteImage(position: Int) {
            deleteImageListener.invoke(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPetImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        with(holder) {
            with(binding) {
                Glide.with(itemView)
                    .load(image)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(imagePetItem)

                fabDelete.setOnClickListener {
                    onClickDeleteImage(position)
                }
            }
        }
    }
}
