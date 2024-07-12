package com.izmirsoftware.petsmatch.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage2Binding
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePetPage2Fragment : Fragment() {
    private val viewModel: CreatePetViewModel by viewModels()
    private var _binding: FragmentCreatePetPage2Binding? = null
    private val binding get() = _binding!!
    private lateinit var petModel: Pet
    private val choose: List<String> by lazy {
        resources.getStringArray(R.array.choose_list).toList()
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
        _binding = FragmentCreatePetPage2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        observeLiveData(viewLifecycleOwner)
        setOnClickItems()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenPopBackStack()
        collectDataFromPopBackStack()
        setDropdownItems()
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataPet.observe(owner) {
                setDropdownItems()
                petModel = it

                with(binding) {
                    viewPetModel = it

                    when (it.duringEstrus) {
                        true -> edittextDuringEstrus.setText(choose[0], false)
                        false -> edittextDuringEstrus.setText(choose[1], false)
                        else -> edittextDuringEstrus.text = null
                    }

                    when (it.vaccinations) {
                        true -> edittextVaccinated.setText(choose[0], false)
                        false -> edittextVaccinated.setText(choose[1], false)
                        else -> edittextVaccinated.text = null
                    }
                }
            }
        }
    }

    private fun setOnClickItems() {
        with(binding) {
            buttonNextPage.setOnClickListener {
                gotoCreatePetPage3(it, collectInputData(petModel))
            }
        }
    }

    private fun collectInputData(pet: Pet): Pet {
        with(binding) {
            pet.apply {
                val duringEstrusText = edittextDuringEstrus.text.toString()
                if (duringEstrusText.isNotBlank()) {
                    duringEstrus = duringEstrusText == choose[0]
                }

                val vaccinatedText = edittextVaccinated.text.toString()
                if (vaccinatedText.isNotBlank()) {
                    vaccinations = vaccinatedText == choose[0]
                }

                personality = edittextPersonality.text.toString()
                interests = edittextAreasInterest.text.toString()
                allergies = edittextAllergies.text.toString()
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

    private fun setDropdownItems() {
        with(binding) {
            edittextDuringEstrus.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    choose.toList()
                )
            )

            edittextVaccinated.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    choose.toList()
                )
            )
        }
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

    private fun gotoCreatePetPage3(view: View, pet: Pet) {
        val direction =
            CreatePetPage2FragmentDirections.actionCreatePetPage2FragmentToCreatePetPage3Fragment(
                pet
            )
        view.findNavController().navigate(direction)
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