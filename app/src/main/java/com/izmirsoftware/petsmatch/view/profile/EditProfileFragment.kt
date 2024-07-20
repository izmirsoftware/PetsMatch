package com.izmirsoftware.petsmatch.view.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.izmirsoftware.petsmatch.adapter.downloadImage
import com.izmirsoftware.petsmatch.databinding.FragmentEditProfileBinding
import com.izmirsoftware.petsmatch.model.UserModel
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.checkPermissionImageGallery
import com.izmirsoftware.petsmatch.util.hideBottomNavigation
import com.izmirsoftware.petsmatch.util.showBottomNavigation
import com.izmirsoftware.petsmatch.util.startLoadingProcess
import com.izmirsoftware.petsmatch.viewmodel.profile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: EditProfileViewModel by viewModels()

    private var selectedProfilePhoto: Uri? = null
    private lateinit var profilePhotoLauncher: ActivityResultLauncher<Intent>

    private var oldUser: UserModel? = null

    private var progressDialog: ProgressDialog? = null
    private lateinit var errorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        errorDialog = AlertDialog.Builder(requireContext()).create()

        binding.ivUserProfilePhoto.setOnClickListener {
            if (checkPermissionImageGallery(requireActivity(), 800)) {
                openProfilePicker()
            }
        }
        binding.btnSave.setOnClickListener{
            getUserDataAndSave()
        }
        setupLaunchers()
        observeLiveData()
        setupDialogs()
    }

    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbEditProfileInfo.visibility = View.GONE
                    binding.tvErrorEditProfile.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.pbEditProfileInfo.visibility = View.VISIBLE
                    binding.tvErrorEditProfile.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.pbEditProfileInfo.visibility = View.GONE
                    binding.tvErrorEditProfile.visibility = View.VISIBLE
                }
            }
        })
        viewModel.uploadMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> it.data?.let { state ->
                    startLoadingProcess(progressDialog)
                }
                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                }
                Status.ERROR -> {
                    progressDialog?.dismiss()
                    errorDialog.setMessage("Login error.\n${it.message}")
                    errorDialog.show()
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                oldUser = userData.copy() // Derin kopyalama
                binding.apply {
                    user = userData
                }
            }
        })
    }

    private fun setupLaunchers() {
        profilePhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { image ->
                        selectedProfilePhoto = image
                        selectedProfilePhoto?.let {
                            downloadImage(binding.ivUserProfilePhoto, image.toString())
                        }
                    }
                }
            }
    }

    private fun openProfilePicker() {
        val imageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        profilePhotoLauncher.launch(imageIntent)
    }

    fun getUserDataAndSave() {
        val newUser = UserModel()
        viewModel.startLoading()
        println("n : "+oldUser?.username)
        newUser.username = binding.etUserName.text.toString().takeIf { it.isNotEmpty() }
        newUser.email = binding.etEmail.text.toString().takeIf { it.isNotEmpty() }
        newUser.phone = binding.etPhone.text.toString().takeIf { it.isNotEmpty() }
        newUser.bio = binding.etBio.text.toString().takeIf { it.isNotEmpty() }

        val uploadMap = viewModel.getMapIfDataChanged(oldUser!!, newUser)
        if (selectedProfilePhoto != null) {
            if (uploadMap.isNotEmpty()) {
                viewModel.uploadUserPhoto(selectedProfilePhoto!!, "profilePhoto", uploadMap)
            } else {
                viewModel.uploadUserPhoto(selectedProfilePhoto!!, "profilePhoto", null)
            }
        } else if (uploadMap.isNotEmpty()) {
            viewModel.updateUserData(uploadMap)
        }
    }
    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Bir hata oluştu, daha sonra tekrar deneyiniz")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }
}
