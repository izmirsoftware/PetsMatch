package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage1Binding
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetPage1ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePetPage1Fragment : Fragment() {
    private val viewModel: CreatePetPage1ViewModel by viewModels()
    private var _binding: FragmentCreatePetPage1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePetPage1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        observeLiveData(viewLifecycleOwner)
        setOnClickItems()

        return root
    }

    private fun setOnClickItems() {
        with(binding) {
            buttonNextPage.setOnClickListener {
                val direction =
                    CreatePetPage1FragmentDirections.actionCreatePetPage1FragmentToCreatePetPage2Fragment()
                Navigation.findNavController(it).navigate(direction)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun observeLiveData(owner: LifecycleOwner) {

    }

    override fun onStart() {
        super.onStart()
        hideBottomNavigation(requireActivity())
    }

    override fun onStop() {
        super.onStop()
        showBottomNavigation(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}