package com.izmirsoftware.petsmatch.view.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.FragmentRegisterBinding
import com.izmirsoftware.petsmatch.viewmodel.login.RegisterViewModel
import com.izmirsoftware.petsmatch.util.Status

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog
    private lateinit var errorDialog: AlertDialog
    private lateinit var verificationDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        errorDialog = AlertDialog.Builder(requireContext()).create()
        verificationDialog = AlertDialog.Builder(requireContext()).create()

        setupDialogs()
        observeLiveData()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            viewModel.signUp(email, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Registration Error")
            setCancelable(false)
            setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ -> dialog.cancel() }
        }

        with(verificationDialog) {
            setTitle("Email Verification")
            setMessage("Verification email sent. Please check your inbox.")
            setCancelable(false)
            setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ -> dialog.cancel() }
        }
    }

    private fun observeLiveData() {
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> progressDialog.show()
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    verificationDialog.show()
                    findNavController().popBackStack()
                }
                Status.ERROR -> {
                    progressDialog.dismiss()
                    errorDialog.setMessage(it.message)
                    errorDialog.show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
