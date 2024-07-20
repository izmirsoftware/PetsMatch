package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.CardPetBinding
import com.izmirsoftware.petsmatch.databinding.CardPetItemBinding
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.view.home.EntryForCreateFragmentDirections
import java.text.SimpleDateFormat
import java.util.Locale

class PetAdapter : RecyclerView.Adapter<PetAdapter.ViewHolder>() {
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

    var petCardList: List<Pet>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class ViewHolder(val binding: CardPetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return petCardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pet = petCardList[position]

        with(holder.binding) {
            textTitle.text = pet.name
            holder.itemView.setOnClickListener{
               if (pet.id != null){
                   val action = EntryForCreateFragmentDirections.actionEntryForCreateFragmentToCreatePostFragment(pet.id!!)
                   Navigation.findNavController(it).navigate(action)
               }
            }
            Glide.with(holder.itemView)
                .load(pet.profileImage)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imagePetProfile)
        }
    }
}
