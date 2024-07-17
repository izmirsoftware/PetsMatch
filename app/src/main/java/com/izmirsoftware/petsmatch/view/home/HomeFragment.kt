package com.izmirsoftware.petsmatch.view.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.adapter.AdapterPetCard
import com.izmirsoftware.petsmatch.databinding.FragmentHomeBinding
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.setupDialogs
import com.izmirsoftware.petsmatch.view.LoginActivity
import com.izmirsoftware.petsmatch.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val adapter: AdapterPetCard by lazy {
        AdapterPetCard()
    }

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.createPetCardModels()
        binding.rvHome.adapter = adapter

        setOnClickItems()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewLifecycleOwner)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataStatus.observe(owner) {
                when (it.status) {
                    Status.SUCCESS -> {}

                    Status.LOADING -> it.data?.let { status -> setProgressBar(status) }

                    Status.ERROR -> {
                        setupDialogs(errorDialog)
                        errorDialog.setMessage("Hata mesajı:\n${it.message}")
                        errorDialog.show()
                    }
                }
            }
            petCardModel.observe(owner) {
                adapter.petCardList = it.toList()
            }
        }
    }

    private fun setOnClickItems() {
        with(binding) {
            fab.setOnClickListener {
                userId?.let {
                    gotoEntryForCreateFragment()
                } ?: run {
                    showLoginMessage()
                }
            }
        }
    }

    private fun showLoginMessage() {
        val dialog = AlertDialog.Builder(requireContext()).create()

        dialog.apply {
            setMessage(resources.getString(R.string.goto_login_message))
            setButton(
                AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.yes)
            ) { dialog, _ ->
                gotoLoginActivity()
                dialog.cancel()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        dialog.show()
    }

    private fun gotoEntryForCreateFragment() {
        val direction =
            HomeFragmentDirections.actionNavigationHomeToEntryForCreateFragment()
        binding.root.findNavController().navigate(direction)
    }

    private fun gotoLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
    }

    private fun setProgressBar(status: Boolean) {
        with(binding) {
            if (status) {
                rvHome.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                rvHome.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}