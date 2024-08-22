package com.izmirsoftware.petsmatch.view.search

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.adapter.AdapterPetCard
import com.izmirsoftware.petsmatch.adapter.AdapterPostCard
import com.izmirsoftware.petsmatch.databinding.FragmentProfileBinding
import com.izmirsoftware.petsmatch.databinding.FragmentSearchBinding
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.setupDialogs
import com.izmirsoftware.petsmatch.view.LoginActivity
import com.izmirsoftware.petsmatch.view.home.HomeFragmentDirections
import com.izmirsoftware.petsmatch.viewmodel.profile.ProfileViewModel
import com.izmirsoftware.petsmatch.viewmodel.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    val viewModel: SearchViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null

    private val adapter = AdapterPetCard()

    private val catAdapter: AdapterPostCard by lazy {
        AdapterPostCard()
    }

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearch.adapter = catAdapter
        binding.ivFilter.setOnClickListener {
            val action = SearchFragmentDirections.actionNavigationSearchToFilterFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        observeLiveData(viewLifecycleOwner)
    }
    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataResult.observe(owner) {
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
            searchResult.observe(owner) {petPosts->
                if (petPosts != null) {
                    catAdapter.petPostList = petPosts
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
                viewModel.logout()
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

    private fun setProgressBar(status: Boolean) {
        with(binding) {
            if (status) {
                pbSearch.visibility = View.VISIBLE
            } else {
                pbSearch.visibility = View.GONE
            }
        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}