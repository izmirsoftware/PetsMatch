package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.petsmatch.databinding.CardPetItemBinding
import com.izmirsoftware.petsmatch.model.Pet

class AdapterPetCard : RecyclerView.Adapter<AdapterPetCard.ViewHolder>() {
    lateinit var onClickCardListener: ((String, View) -> Unit)

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

    inner class ViewHolder(val binding: CardPetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onClickCard(petId: String, view: View) {
            onClickCardListener.invoke(petId, view)
        }
    }

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
        val pet = petList[position]
        with(holder) {
            binding.viewPet = pet

            pet.id?.let { id ->
                itemView.setOnClickListener { v ->
                    onClickCard(id, v)
                }
            }
        }
    }
}
