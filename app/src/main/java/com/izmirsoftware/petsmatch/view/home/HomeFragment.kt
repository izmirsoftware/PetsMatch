package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.izmirsoftware.petsmatch.adapter.AdapterPetCard
import com.izmirsoftware.petsmatch.databinding.FragmentHomeBinding
import com.izmirsoftware.petsmatch.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val adapter: AdapterPetCard by lazy {
        AdapterPetCard()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.createPetCardModels()
        binding.rvHome.adapter = adapter

        setOnClickItems()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewLifecycleOwner)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        viewModel.petCardModel.observe(owner) {
            adapter.petCardList = it.toList()
        }
    }

    private fun setOnClickItems() {
        with(binding) {
            fab.setOnClickListener {
                val direction =
                    HomeFragmentDirections.actionNavigationHomeToEntryForCreateFragment()
                Navigation.findNavController(it).navigate(direction)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}