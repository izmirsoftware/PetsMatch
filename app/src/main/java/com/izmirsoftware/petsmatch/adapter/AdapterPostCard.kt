package com.izmirsoftware.petsmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.petsmatch.databinding.CardPostItemBinding
import com.izmirsoftware.petsmatch.model.PetCardModel
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterPostCard : RecyclerView.Adapter<AdapterPostCard.ViewHolder>() {
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

    inner class ViewHolder(val binding: CardPostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPostItemBinding.inflate(
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
            viewPetCardModel = petCardModel

            petCardModel.petPost?.date?.let {
                textDate.text = buildString {
                    append("İlan tarihi\n")
                    append(
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(it)
                    )
                }
            }
        }
    }
}
