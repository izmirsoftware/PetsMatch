package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage3Binding
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetPage3ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePetPage3Fragment : Fragment() {
    private val viewModel: CreatePetPage3ViewModel by viewModels()
    private var _binding: FragmentCreatePetPage3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePetPage3Binding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}