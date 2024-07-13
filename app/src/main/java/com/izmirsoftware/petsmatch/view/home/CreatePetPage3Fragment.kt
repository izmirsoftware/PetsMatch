package com.izmirsoftware.petsmatch.view.home

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.adapter.AdapterPetImageCard
import com.izmirsoftware.petsmatch.databinding.CustomDialogChooseImageSourceBinding
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePetPage3Binding
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.checkPermissionImageCamera
import com.izmirsoftware.petsmatch.util.checkPermissionImageGallery
import com.izmirsoftware.petsmatch.util.compressJpegInBackground
import com.izmirsoftware.petsmatch.util.convertUriToBitmap
import com.izmirsoftware.petsmatch.util.createImageUri
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.setupDialogs
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePetPage3Fragment : Fragment() {
    private val viewModel: CreatePetViewModel by viewModels()
    private var _binding: FragmentCreatePetPage3Binding? = null
    private val binding get() = _binding!!
    private val adapter: AdapterPetImageCard by lazy { AdapterPetImageCard() }
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var multipleImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageUri: Uri
    private val uploadedProfileImage = mutableListOf<String>()
    private val selectedUriProfileImage = mutableListOf<String>()
    private val selectedByteArrayProfileImage = mutableListOf<ByteArray>()
    private val uploadedImages = mutableListOf<String>()
    private val selectedUriImages = mutableListOf<String>()
    private val selectedByteArrayImages = mutableListOf<ByteArray>()
    private val dialogChooseImageSource: Dialog by lazy { createDialogChooseImageSource() }
    private val errorDialog: AlertDialog by lazy { AlertDialog.Builder(requireContext()).create() }

    private lateinit var petModel: Pet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: CreatePetPage1FragmentArgs by navArgs()
        petModel = args.pet // argüman olarak gelen pet model fragment değişkenine aktarıldı
        viewModel.setPetModel(petModel) // gelen

        setupLaunchers()// veri görünüme aktarılması için viewmodel'e gönderiliyor
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
        with(viewModel) {
            liveDataStatus.observe(owner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { status ->
                            if (status) {
                                gotoEntryForCreate(binding.root)
                            }
                        }
                    }

                    Status.LOADING -> it.data?.let { status -> setProgressBar(status) }
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

            liveDataPet.observe(owner) {

            }
            liveDataImages.observe(owner) { imageList ->
                adapter.images = imageList.toList()
            }
        }
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

    private fun setOnClickItems() {
        with(binding) {
            buttonSave.setOnClickListener {
                viewModel.addImageAndPetToFirebase(
                    selectedByteArrayImages,
                    petModel,
                    uploadedImages
                )
            }
        }
    }

    private fun collectInputData(pet: Pet): Pet {
        with(binding) {
            pet.apply {


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

    //TODO: profil resmi seçmek için ayrı intent oluştur
    private fun openImagePicker() {
        val imageIntent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        imageLauncher.launch(imageIntent)
    }

    private fun openMultipleImagePicker() {
        val imageIntent = Intent()
        imageIntent.setType("image/*")
        imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imageIntent.setAction(Intent.ACTION_GET_CONTENT)

        multipleImageLauncher.launch(
            Intent.createChooser(
                imageIntent, "Resimleri seçin"
            )
        )
    }

    private fun openCamera() {
        imageUri = createImageUri(requireContext())
        cameraLauncher.launch(imageUri)
    }

    private fun createDialogChooseImageSource(): Dialog {
        val view = CustomDialogChooseImageSourceBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(view.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.cardCameraImageSource.setOnClickListener {
            if (checkPermissionImageCamera(requireActivity(), 800)) {
                openCamera()
                dialog.dismiss()
            }
        }

        view.cardGalleryImageSource.setOnClickListener {
            if (checkPermissionImageGallery(requireActivity(), 801)) {
                openImagePicker()
                openMultipleImagePicker()
                dialog.dismiss()
            }
        }

        view.imageCloseChooseImageSource.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    private fun setupLaunchers() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                selectedUriImages.add(imageUri.toString())
                adapter.images = selectedUriImages.toList()

                val bitmap = convertUriToBitmap(imageUri, requireActivity())
                compressJpegInBackground(bitmap) { byteArrayImage ->
                    selectedByteArrayImages.add(byteArrayImage)
                }
            }
        }

        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let {
                        selectedUriImages.add(it.toString())
                        adapter.images = selectedUriImages.toList()

                        val bitmap = convertUriToBitmap(it, requireActivity())
                        compressJpegInBackground(bitmap) { byteArrayImage ->
                            selectedByteArrayImages.add(byteArrayImage)
                        }
                    }
                }
            }

        multipleImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.clipData?.let { data ->
                        val size = data.itemCount
                        for (i in 0..<size) {
                            val imageUri = data.getItemAt(i).uri
                            selectedUriImages.add(imageUri.toString())
                            adapter.images = selectedUriImages.toList()

                            val bitmap = convertUriToBitmap(imageUri, requireActivity())
                            compressJpegInBackground(bitmap) { byteArrayImage ->
                                selectedByteArrayImages.add(byteArrayImage)
                            }
                        }
                    }
                }
            }
    }

    private fun gotoEntryForCreate(view: View) {
        val direction =
            CreatePetPage3FragmentDirections.actionCreatePetPage3FragmentToEntryForCreateFragment()
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