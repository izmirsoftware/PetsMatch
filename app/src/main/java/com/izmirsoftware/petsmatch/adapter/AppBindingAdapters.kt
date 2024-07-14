package com.izmirsoftware.petsmatch.adapter

import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter


@BindingAdapter(value = ["text", "filter"])
fun setText(view: AutoCompleteTextView, text: String?, filter: Boolean?) {
    view.setText(text, filter!!)
}