package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.petsmatch.databinding.CardPostItemBinding
import com.izmirsoftware.petsmatch.databinding.RowPetBinding
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.model.PetPost
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterPostCard : RecyclerView.Adapter<AdapterPostCard.ViewHolder>() {
    private val diffUtil = object : DiffUtil.ItemCallback<PetPost>() {
        override fun areItemsTheSame(oldItem: PetPost, newItem: PetPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PetPost, newItem: PetPost): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(
        this,
        diffUtil
    )

    var petPostList: List<PetPost>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class ViewHolder(val binding: RowPetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowPetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return petPostList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val petPost = petPostList[position]

        with(holder.binding) {
            post = petPost
        }
    }
}
