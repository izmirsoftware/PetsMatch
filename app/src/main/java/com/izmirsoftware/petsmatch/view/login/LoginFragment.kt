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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.izmirsoftware.petsmatch.databinding.FragmentLoginBinding
import com.izmirsoftware.petsmatch.viewmodel.login.LoginViewModel
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.startLoadingProcess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var progressDialog: ProgressDialog? = null

    private lateinit var errorDialog: AlertDialog
    private lateinit var verifiedEmailDialog: AlertDialog
    private lateinit var forgotPasswordDialog: AlertDialog
    private lateinit var forgotPasswordSuccessDialog: AlertDialog
    private lateinit var verificationEmailSentDialog: AlertDialog
    private lateinit var verificationEmailSentErrorDialog: AlertDialog

    val RC_SIGN_IN = 20
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewLifecycleOwner)

        progressDialog = ProgressDialog(requireContext())

        errorDialog = AlertDialog.Builder(requireContext()).create()
        verifiedEmailDialog = AlertDialog.Builder(requireContext()).create()
        forgotPasswordDialog = AlertDialog.Builder(requireContext()).create()
        forgotPasswordSuccessDialog = AlertDialog.Builder(requireContext()).create()
        verificationEmailSentDialog = AlertDialog.Builder(requireContext()).create()
        verificationEmailSentErrorDialog = AlertDialog.Builder(requireContext()).create()

        setProgressBar(false)
        setupDialogs()
/*
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        binding.layoutGoogle.setOnClickListener {
           // googleSignIn()
        }
*/

        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (email.isNotEmpty() && password.length > 5) {
                    viewModel?.login(email, password)
                } else if (email.isEmpty()) {
                    etEmail.error = "Please enter an email address"
                } else {
                    etPassword.error = "Please enter a password (at least 6 characters)"
                }
            }

            tvForgotPassword.setOnClickListener {
                forgotPasswordDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Yes"
                ) { _, _ ->
                    val email = etEmail.text.toString().trim()
                    if (email.isNotEmpty()) {
                        viewModel?.asdasd()
                    } else {
                        etEmail.error = "Please enter an email address"
                    }
                }
                forgotPasswordDialog.show()
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        verifyEmail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun verifyEmail() {
        viewModel.getUser()?.let {
            if (it.isEmailVerified) {
                gotoHome()
            } else {
                verifiedEmailDialog.show()
            }
        }
    }

    private fun gotoHome() {
     /*
        val intent = Intent(requireContext(), MainActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
      */
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            authState.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state ->
                        setProgressBar(state)
                        startLoadingProcess(progressDialog)
                    }
                    Status.SUCCESS -> {
                        verifyEmail()
                        progressDialog?.dismiss()
                    }
                    Status.ERROR -> {
                        progressDialog?.dismiss()
                        errorDialog.setMessage("Login error.\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            forgotPassword.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        forgotPasswordSuccessDialog.show()
                    }
                    Status.ERROR -> {
                        forgotPasswordDialog.setMessage("Password reset error.\n${it.message}")
                    }
                }
            }

            verificationEmailSent.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        verificationEmailSentDialog.show()
                    }
                    Status.ERROR -> {
                        verificationEmailSentErrorDialog.show()
                    }
                }
            }
        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Login Error")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(verifiedEmailDialog) {
            setTitle("Email Verification")
            setMessage("Do you want to verify your email?")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Yes"
            ) { _, _ ->
                viewModel.sendVerificationEmail()
                viewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "No"
            ) { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
            }
        }

        with(verificationEmailSentDialog) {
            setTitle("Email Verification")
            setMessage("Verification email sent successfully.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(verificationEmailSentErrorDialog) {
            setTitle("Email Verification Error")
            setMessage("Failed to send verification email. Would you like to try again?")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Yes"
            ) { _, _ ->
                viewModel.sendVerificationEmail()
                viewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "No"
            ) { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
            }
        }

        with(forgotPasswordDialog) {
            setTitle("Forgot Password")
            setMessage("Password reset link sent to your email address.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Close"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(forgotPasswordSuccessDialog) {
            setTitle("Forgot Password")
            setMessage("Your new password has been sent to your email address.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun setProgressBar(isVisible: Boolean) {
        binding.pbSignIn.visibility = if (isVisible) View.VISIBLE else View.GONE
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
                task.getResult(ApiException::class.java)?.idToken?.let { token ->
                    viewModel.signInWithGoogle(token)
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google sign in failed: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
