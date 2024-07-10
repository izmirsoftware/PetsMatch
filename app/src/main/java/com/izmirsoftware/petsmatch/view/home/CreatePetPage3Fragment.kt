package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.petsmatch.adapter.AdapterPetImageCard
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage3Binding
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePetPage3Fragment : Fragment() {
    private val viewModel: CreatePetViewModel by viewModels()
    private var _binding: FragmentCreatePetPage3Binding? = null
    private val binding get() = _binding!!
    private val adapter: AdapterPetImageCard by lazy {
        AdapterPetImageCard()
    }

    private var petModel: Pet = Pet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: CreatePetPage1FragmentArgs by navArgs()
        petModel = args.pet // argüman olarak gelen pet model fragment değişkenine aktarıldı
        viewModel.setPetModel(petModel) // gelen veri görünüme aktarılması için viewmodel'e gönderiliyor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePetPage3Binding.inflate(inflater, container, false)
        val root: View = binding.root

        observeLiveData(viewLifecycleOwner)
        setOnClickItems()

        binding.rvPetImages.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPetImagesSample()
        listenPopBackStack()
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        viewModel.liveDataImages.observe(owner) { imageList ->
            adapter.images = imageList.toList()
        }
    }

    private fun setOnClickItems() {
        with(binding) {
            buttonSave.setOnClickListener {
                //TODO: verileri veritabanına kaydet
                gotoEntryForCreate(it)
            }
        }
    }

    private fun collectInputData(pet: Pet): Pet {
        with(binding) {
            pet.apply {
//                if (id.isNullOrEmpty()) {
//                    id = UUID.randomUUID().toString()
//                }
//                if (edittextGenus.text.equals(genusList[0])) {
//                    genus = Genus.CAT
//                } else {
//                    genus = Genus.DOG
//                }
//
//                if (edittextGender.text.equals(genderList[0])) {
//                    gender = Gender.MALE
//                } else {
//                    gender = Gender.FEMALE
//                }
//
//                breed = edittextBreed.text?.toString()
//                name = edittextName.text?.toString()
//                age = edittextAge.text?.toString()?.toIntOrNull()
//                color = edittextColor.text?.toString()

            }
        }

        return pet
    }

    private fun listenPopBackStack() {
        //Telefon geri tuşunu dinliyoruz
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val navController = findNavController()

            navController.previousBackStackEntry?.savedStateHandle?.set(
                "petModelPopBackStack",
                petModel
            )

            //geri tuşuna basıldığında önceki sayfaya pet modeli gönderiyoruz
            navController.popBackStack()
        }
    }

    private fun gotoEntryForCreate(view: View) {
        val direction =
            CreatePetPage3FragmentDirections.actionCreatePetPage3FragmentToEntryForCreateFragment()
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