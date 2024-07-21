package com.izmirsoftware.petsmatch.view.search

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.adapter.AdapterPetCard
import com.izmirsoftware.petsmatch.databinding.FragmentProfileBinding
import com.izmirsoftware.petsmatch.databinding.FragmentSearchBinding
import com.izmirsoftware.petsmatch.viewmodel.profile.ProfileViewModel
import com.izmirsoftware.petsmatch.viewmodel.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    val viewModel: SearchViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null

    private val adapter = AdapterPetCard()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

}