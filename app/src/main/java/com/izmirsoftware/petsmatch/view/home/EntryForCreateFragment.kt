package com.izmirsoftware.petsmatch.view.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.adapter.AdapterPetCard
import com.izmirsoftware.petsmatch.databinding.FragmentEntryForCreateBinding
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.setupDialogs
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.EntryForCreateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryForCreateFragment : Fragment() {
    private val viewModel: EntryForCreateViewModel by viewModels()
    private var _binding: FragmentEntryForCreateBinding? = null
    private val binding get() = _binding!!

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val adapter: AdapterPetCard by lazy {
        AdapterPetCard()
    }

    private val errorDialog: AlertDialog by lazy { AlertDialog.Builder(requireContext()).create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId?.let { id ->
            viewModel.getPetsByUserId(id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryForCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvEntryCreate.adapter = adapter

        setOnClickItems()
        observeLiveData(viewLifecycleOwner)

        return root
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataResult.observe(owner) {
                when (it.status) {
                    Status.SUCCESS -> {}
                    Status.LOADING -> {
                        binding.setProgressBar = it.data
                    }

                    Status.ERROR -> {
                        setupDialogs(errorDialog)
                        errorDialog.setMessage(buildString {
                            append(R.string.error_message)
                            append("\n")
                            append(it.message)
                        })
                        errorDialog.show()
                    }
                }
            }

            liveDataPets.observe(owner) { dbPetList ->
                adapter.petList = dbPetList.toList()
                if (dbPetList.isNotEmpty()) {
                    binding.textEntryCreateEmptyMessage.visibility = View.GONE
                }
            }
        }
    }

    private fun setOnClickItems() {
        with(binding) {
            fab.setOnClickListener {
                gotoCreatePetPage1(it)
            }

            adapter.onClickCardListener = { petId, view ->
                val direction =
                    EntryForCreateFragmentDirections.actionEntryForCreateFragmentToCreatePostFragment(
                        petId
                    )
                view.findNavController().navigate(direction)
            }
        }
    }

    private fun gotoCreatePetPage1(view: View) {
        val direction =
            EntryForCreateFragmentDirections.actionEntryForCreateFragmentToCreatePetPage1Fragment(
                Pet()
            )
        Navigation.findNavController(view).navigate(direction)
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