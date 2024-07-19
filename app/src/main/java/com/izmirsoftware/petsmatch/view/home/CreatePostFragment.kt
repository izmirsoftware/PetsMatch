package com.izmirsoftware.petsmatch.view.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePostBinding
import com.izmirsoftware.petsmatch.model.Location
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.getCurrentDate
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.setupDialogs
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePostViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: CreatePostViewModel by viewModels()
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext()).create()
    }

    private var pet: Pet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: CreatePostFragmentArgs by navArgs()
        viewModel.getPetByIdFromFirestore(args.petId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setOnClickItems()
        setDropDownItems()
        observeLiveData(viewLifecycleOwner)

        return root
    }

    private fun setDropDownItems() {
        //TODO: şehir ve ilçeyi listeden al
    }

    private fun setOnClickItems() {
        binding.buttonSave.setOnClickListener {
            //TODO: mecbur girilmesi gereken alanları belirle
            viewModel.addPostToFirestore(collectData(PetPost()))
        }
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataResult.observe(owner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { status ->
                            if (status) {
                                gotoMainFragment(binding.root)
                            }
                        }
                    }

                    Status.LOADING -> it.data?.let { status -> setProgressBar(status) }

                    Status.ERROR -> {
                        setupDialogs(errorDialog)
                        errorDialog.setMessage("Hata mesajı:\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            liveDataPet.observe(owner) {
                pet = it
                binding.viewPet = it
            }
        }
    }

    private fun collectData(petPost: PetPost): PetPost {
        pet?.id?.let { petModelId ->
            with(binding) {
                petPost.apply {
                    if (id == null) {
                        id = UUID.randomUUID().toString()
                    }
                    title = editTextTitle.text.toString()
                    description = editTextDescription.text.toString()
                    date = getCurrentDate()
                    location = Location(
                        city = editTextCity.text.toString(),
                        district = editTextDistrict.text.toString()
                    )
                    petId = petModelId
                }
            }
        }

        return petPost
    }

    private fun setProgressBar(status: Boolean) {
        with(binding) {
            if (status) {
                buttonSave.text = null
                buttonSave.isEnabled = false
                progressBar.visibility = View.VISIBLE
            } else {
                buttonSave.text = resources.getString(R.string.save)
                buttonSave.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }
    }


    private fun gotoMainFragment(root: View) {
        val direction = CreatePostFragmentDirections.actionCreatePostFragmentToNavigationHome()
        root.findNavController().navigate(direction)
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