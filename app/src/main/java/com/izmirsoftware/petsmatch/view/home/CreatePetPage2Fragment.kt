package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage2Binding
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetPage2ViewModel

class CreatePetPage2Fragment : Fragment() {
    private val viewModel: CreatePetPage2ViewModel by viewModels()
    private var _binding: FragmentCreatePetPage2Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData(viewLifecycleOwner)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePetPage2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}