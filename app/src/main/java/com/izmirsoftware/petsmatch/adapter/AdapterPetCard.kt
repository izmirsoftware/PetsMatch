package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.petsmatch.databinding.CardPetItemBinding
import com.izmirsoftware.petsmatch.model.Pet

class AdapterPetCard : RecyclerView.Adapter<AdapterPetCard.ViewHolder>() {
    private val diffUtil = object : DiffUtil.ItemCallback<Pet>() {
        override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(
        this,
        diffUtil
    )

    var petList: List<Pet>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class ViewHolder(val binding: CardPetItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPetItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewPet = petList[position]
        //TODO: evcil hayvanların doğum tarihlerini al
        //TODO: doğum tarihinden yaşı hesapla
    }
}
