package com.izmirsoftware.petsmatch.view.search

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.lifecycle.Observer
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.viewmodel.search.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment() {

    companion object {
        fun newInstance() = FilterFragment()
    }

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerGenus = view.findViewById<Spinner>(R.id.spinnerGenus)
        val spinnerGender = view.findViewById<Spinner>(R.id.spinnerGender)
        val spinnerAge = view.findViewById<Spinner>(R.id.spinnerAge)
        val spinnerBreed = view.findViewById<Spinner>(R.id.spinnerBreed)
        val spinnerColor = view.findViewById<Spinner>(R.id.spinnerColor)
        val btnFilter = view.findViewById<Button>(R.id.btnFilter)

        btnFilter.setOnClickListener {
            val selectedGenus = spinnerGenus.selectedItem.toString().takeIf { it.isNotEmpty() }
            val selectedGender = spinnerGender.selectedItem.toString().takeIf { it.isNotEmpty() }
            val selectedAge = spinnerAge.selectedItem.toString().takeIf { it.isNotEmpty() }
            val selectedBreed = spinnerBreed.selectedItem.toString().takeIf { it.isNotEmpty() }
            val selectedColor = spinnerColor.selectedItem.toString().takeIf { it.isNotEmpty() }

            viewModel.filterPets(selectedGenus, selectedGender, selectedAge, selectedBreed, selectedColor, 50L)
        }
    }
}