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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.izmirsoftware.petsmatch.view.MainActivity
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.FragmentRegisterBinding
import com.izmirsoftware.petsmatch.viewmodel.login.RegisterViewModel
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.startLoadingProcess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var verificationDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null
    val RC_SIGN_IN = 20
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private var progressDialog: ProgressDialog? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())


        errorDialog = AlertDialog.Builder(requireContext()).create()
        verificationDialog = AlertDialog.Builder(requireContext()).create()
/*
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        binding.btnGoogle.setOnClickListener {
            googleSignIn()
        }*/
      /*
        binding.tvGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
       */

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.signUp(name,email, password, confirmPassword)
        }
        binding.tvGOtoLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        setupDialogs()
        observeLiveData()
    }


    private fun observeLiveData() {
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.btnRegister.isEnabled = true
                    progressDialog?.dismiss()
                }

                Status.LOADING -> {
                    startLoadingProcess(progressDialog)
                    binding.pbRegister.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }

                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.btnRegister.isEnabled = true
                    if (it.data == true) {
                        enter()
                    }
                }
            }
        })

        viewModel.registrationError.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    if (it.data == true) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    } else {
                        binding.etConfirmPassword.error = it.message
                    }
                }

                else -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.isVerificationEmailSent.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    verificationDialog?.show()
                }

                Status.ERROR -> {
                    errorDialog?.show()
                }

                else -> {
                    errorDialog?.show()
                }
            }
        })

    }

    private fun enter() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun setupDialogs() {
        verificationDialog?.setTitle("Email Doğrulama")
        verificationDialog?.setMessage("Doğrulama e-postası gönderildi. Lütfen e-posta kutunuzu kontrol edin.")
        verificationDialog?.setCancelable(false)

        verificationDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->
            findNavController().popBackStack()
        }

        errorDialog?.setTitle("Hata")
        errorDialog?.setMessage("e-posta gönderilemedi")
        errorDialog?.setCancelable(false)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tekrar gönder") { _, _ ->

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun googleSignIn() {
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.addOnSuccessListener {
                    val account = it.requestExtraScopes()
                    viewModel.signInWithGoogle(account.idToken)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Giriş Yapılamadı",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Giriş Yapılamadı : " + e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}