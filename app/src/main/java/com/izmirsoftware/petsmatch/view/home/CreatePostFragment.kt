package com.izmirsoftware.petsmatch.view.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.izmirsoftware.petsmatch.databinding.FragmentCreatePostBinding
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.setupDialogs
import com.izmirsoftware.petsmatch.viewmodel.home.CreatePostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: CreatePostViewModel by viewModels()
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        observeLiveData(viewLifecycleOwner)

        return root
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
        }
    }

    private fun setProgressBar(status: Boolean) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}