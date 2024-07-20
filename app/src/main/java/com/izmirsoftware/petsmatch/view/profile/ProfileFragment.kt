package com.izmirsoftware.petsmatch.view.profile

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.adapter.AdapterPostCard
import com.izmirsoftware.petsmatch.databinding.FragmentProfileBinding
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProfileViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null

    private val adapter = AdapterPostCard()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel.createPetCardModels()
        binding.rvProfile.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        binding.ivUserImage.setOnClickListener {
            goToEditProfileDetails(it)
        }

        // FIXME: test amaçlı eklendi
        binding.ivUserImage.setOnLongClickListener {
            viewModel.logout()
            gotoMainFragment(it)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
    }


    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.tvErrorProfile.visibility = View.GONE
                    binding.layoutProfile.visibility = View.VISIBLE
                }

                Status.LOADING -> {
                    binding.pbProfile.visibility = View.VISIBLE
                    binding.tvErrorProfile.visibility = View.GONE
                    binding.layoutProfile.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.tvErrorProfile.visibility = View.VISIBLE
                    binding.layoutProfile.visibility = View.GONE
                }
            }
        })

        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
        })
        viewModel.petCardModel.observe(viewLifecycleOwner) {
            adapter.petCardList = it.toList()
        }
    }

    private fun gotoMainFragment(view: View) {
        view.findNavController().navigate(R.id.action_global_navigation_home)
    }

    private fun goToEditProfileDetails(view: View) {
        val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}