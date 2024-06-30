package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.CardPetItemBinding
import com.izmirsoftware.petsmatch.model.PetCardModel
import java.text.SimpleDateFormat
import java.util.*

class AdapterPetCard : RecyclerView.Adapter<AdapterPetCard.ViewHolder>() {
    private val diffUtil = object : DiffUtil.ItemCallback<PetCardModel>() {
        override fun areItemsTheSame(oldItem: PetCardModel, newItem: PetCardModel): Boolean {
            return oldItem.petPost?.id == newItem.petPost?.id
        }

        override fun areContentsTheSame(oldItem: PetCardModel, newItem: PetCardModel): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(
        this,
        diffUtil
    )

    var petCardList: List<PetCardModel>
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
        return petCardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val petCardModel = petCardList[position]

        with(holder.binding) {
            textTitle.text = petCardModel.petPost?.title
            textDescription.text = petCardModel.petPost?.description
            textLocation.text = buildString {
                append(petCardModel.petPost?.location?.district)
                append(" / ")
                append(petCardModel.petPost?.location?.city)
            }

            textRating.text = petCardModel.owner?.comments?.getOrNull(0)?.rating.toString()

            petCardModel.petPost?.date?.let {
                textDate.text = buildString {
                    append("İlan tarihi: ")
                    append(SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(it))
                }
            }

            Glide.with(holder.itemView)
                .load(petCardModel.pet?.profileImage)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imagePetProfile)
        }
    }
}
