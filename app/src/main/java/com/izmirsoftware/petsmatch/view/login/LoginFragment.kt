package com.izmirsoftware.petsmatch.view.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.izmirsoftware.petsmatch.MainActivity
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.FragmentLoginBinding
import com.izmirsoftware.petsmatch.viewmodel.login.LoginViewModel
import com.izmirsoftware.petsmatch.util.Status

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog
    private lateinit var errorDialog: AlertDialog
    private lateinit var verifiedEmailDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        errorDialog = AlertDialog.Builder(requireContext()).create()
        verifiedEmailDialog = AlertDialog.Builder(requireContext()).create()

        setupDialogs()
        observeLiveData()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Login Error")
            setCancelable(false)
            setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ -> dialog.cancel() }
        }

        with(verifiedEmailDialog) {
            setTitle("Email Verification")
            setMessage("Please verify your email address.")
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
                    if (viewModel.isEmailVerified()) {
                        val intent = Intent(requireActivity(),MainActivity::class.java)
                        requireActivity().finish()
                        startActivity(intent)
                    } else {
                        verifiedEmailDialog.show()
                    }
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
