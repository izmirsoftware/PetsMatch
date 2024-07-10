package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage1Binding
import com.izmirsoftware.petsmatch.model.Gender
import com.izmirsoftware.petsmatch.model.Genus
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CreatePetPage1Fragment : Fragment() {
    private val viewModel: CreatePetViewModel by viewModels()
    private var _binding: FragmentCreatePetPage1Binding? = null
    private val binding get() = _binding!!
    private var petModel: Pet = Pet()
    private val genusList = resources.getStringArray(R.array.genus_list).toList()
    private val genderList = resources.getStringArray(R.array.gender_list).toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: CreatePetPage1FragmentArgs by navArgs()
        petModel = args.pet // argüman olarak gelen pet model fragment değişkenine aktarıldı
        viewModel.setPetModel(petModel) // gelen veri görünüme aktarılması için viewmodel'e gönderiliyor
    }

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
                gotoCreatePetPage2(it, collectInputData(petModel))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectDataFromPopBackStack()

    }

    private fun observeLiveData(owner: LifecycleOwner) {
        viewModel.liveDataPet.observe(owner) {
            petModel = it

            with(binding) {
                viewPetModel = it

                // Evcil hayvan türünü seçili dile göre getiriyoruz
                when (it.genus) {
                    Genus.CAT -> edittextGenus.setText(genusList[0], false)
                    Genus.DOG -> edittextGenus.setText(genusList[1], false)
                    else -> edittextGenus.text = null
                }

                // Evcil hayvan cinsini seçili dile göre getiriyoruz
                when (it.gender) {
                    Gender.MALE -> edittextGenus.setText(genderList[0], false)
                    Gender.FEMALE -> edittextGenus.setText(genderList[1], false)
                    else -> edittextGender.text = null
                }
            }

        }
    }

    private fun collectInputData(pet: Pet): Pet {
        with(binding) {
            pet.apply {
                if (id.isNullOrEmpty()) {
                    id = UUID.randomUUID().toString()
                }
                if (edittextGenus.text.equals(genusList[0])) {
                    genus = Genus.CAT
                } else {
                    genus = Genus.DOG
                }

                if (edittextGender.text.equals(genderList[0])) {
                    gender = Gender.MALE
                } else {
                    gender = Gender.FEMALE
                }

                breed = edittextBreed.text?.toString()
                name = edittextName.text?.toString()
                age = edittextAge.text?.toString()?.toIntOrNull()
                color = edittextColor.text?.toString()

            }
        }

        return pet
    }


    private fun collectDataFromPopBackStack() {
        // bir sonraki sayfada geri tuşuna basıldığında gönderilen argümanı yakalıyoruz
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Pet>("petModelPopBackStack")
            ?.observe(viewLifecycleOwner) { data ->
                petModel = data
                viewModel.setPetModel(data)
            }
    }

    private fun gotoCreatePetPage2(view: View, pet: Pet) {
        val direction =
            CreatePetPage1FragmentDirections.actionCreatePetPage1FragmentToCreatePetPage2Fragment(
                pet
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