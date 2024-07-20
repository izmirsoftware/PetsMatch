package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
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

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@AndroidEntryPoint
class CreatePetPage1Fragment : Fragment() {
    private val viewModel: CreatePetViewModel by viewModels()
    private var _binding: FragmentCreatePetPage1Binding? = null
    private val binding get() = _binding!!
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var petModel = Pet()
    private val genusList: List<String> by lazy {
        resources.getStringArray(R.array.genus_list).toList()
    }
    private val genderList: List<String> by lazy {
        resources.getStringArray(R.array.gender_list).toList()
    }

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
        val view = binding.root

        observeLiveData(viewLifecycleOwner)
        setOnClickItems()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDropdownItems()
        collectDataFromPopBackStack()
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        viewModel.liveDataPet.observe(owner) {
            setDropdownItems()
            petModel = it

            with(binding) {
                viewPetModel = it

                // Evcil hayvan türünü seçili dile göre getiriyoruz
                when (it.genus) {
                    Genus.CAT -> {
                        edittextGenus.setText(genusList[0], false)
                        chooseBreedList(0)
                    }

                    Genus.DOG -> {
                        edittextGenus.setText(genusList[1], false)
                        chooseBreedList(1)
                    }

                    else -> edittextGenus.text = null
                }

                // Evcil hayvan cinsini seçili dile göre getiriyoruz
                when (it.gender) {
                    Gender.MALE -> edittextGender.setText(genderList[0], false)
                    Gender.FEMALE -> edittextGender.setText(genderList[1], false)
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

                val genusText = edittextGenus.text.toString()

                if (genusText.isNotBlank()) {
                    genus = if (genusText == genusList[0]) {
                        Genus.CAT
                    } else {
                        Genus.DOG
                    }
                }

                val genderText = edittextGender.text.toString()

                if (genderText.isNotBlank()) {
                    gender = if (genderText == genderList[0]) {
                        Gender.MALE
                    } else {
                        Gender.FEMALE
                    }
                }

                breed = edittextBreed.text?.toString()
                name = edittextName.text?.toString()
                age = edittextAge.text?.toString()?.toIntOrNull()
                color = edittextColor.text?.toString()
                ownerId = userId
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
        view.findNavController().navigate(direction)
    }


    private fun setDropdownItems() {
        with(binding) {
            edittextGenus.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    genusList.toList()
                )
            )

            edittextGender.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    genderList.toList()
                )
            )

            edittextGenus.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    edittextBreed.text = null

                    chooseBreedList(position)
                }
        }
    }

    private fun chooseBreedList(index: Int) {
        var breedList: List<String> = listOf()

        when (index) {
            0 -> breedList = resources.getStringArray(R.array.cat_breed_list).toList()


            1 -> breedList = resources.getStringArray(R.array.dog_breed_list).toList()
        }

        binding.edittextBreed.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                breedList.toList()
            )
        )
    }

    private fun setOnClickItems() {
        with(binding) {
            buttonNextPage.setOnClickListener {
                gotoCreatePetPage2(it, collectInputData(petModel))
            }
        }
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