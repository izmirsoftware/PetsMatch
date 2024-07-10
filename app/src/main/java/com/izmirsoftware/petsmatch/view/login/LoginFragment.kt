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
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.izmirsoftware.petsmatch.databinding.FragmentLoginBinding
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.startLoadingProcess
import com.izmirsoftware.petsmatch.view.MainActivity
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import com.izmirsoftware.petsmatch.viewmodel.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel : LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var progressDialog: ProgressDialog? = null

    private lateinit var errorDialog: AlertDialog
    private lateinit var verifiedEmailDialog: AlertDialog
    private lateinit var forgotPasswordDialog: AlertDialog
    private lateinit var forgotPasswordSuccessDialog: AlertDialog
    private lateinit var verificationEmailSentDialog: AlertDialog
    private lateinit var verificationEmailSentErrorDialog: AlertDialog
    private lateinit var emailVerificationReminderDialog: AlertDialog


    val RC_SIGN_IN = 20
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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
        emailVerificationReminderDialog = AlertDialog.Builder(requireContext()).create()

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
                    println("1")
                    loginViewModel.login(email,password)
                } else if (email.isEmpty()) {
                    println("e")
                    etEmail.error = "Please enter an email address"
                } else {
                    println("e")
                    etPassword.error = "Please enter a password (at least 6 characters)"
                }
            }

            tvForgotPassword.setOnClickListener {
                forgotPasswordDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Yes"
                ) { _, _ ->
                    val email = etEmail.text.toString().trim()
                    if (email.isNotEmpty()) {
                        loginViewModel.forgotPassword(email)
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
        loginViewModel.getUser()?.let {
            if (it.isEmailVerified) {
                gotoHome()
            } else {
                verifiedEmailDialog.show()
            }
        }
    }

    private fun gotoHome() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(loginViewModel) {
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
            setTitle("Giriş Hatası")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(verifiedEmailDialog) {
            setTitle("E-posta Doğrulama")
            setMessage("Lütfen e-posta adresinizi doğrulayın \nDoğrulama linki göndermek ister misiniz?")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Gönder"
            ) { _, _ ->
                loginViewModel.sendVerificationEmail()
                loginViewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Hayır"
            ) { dialog, _ ->
                dialog.cancel()
                loginViewModel.signOut()
            }
        }

        with(verificationEmailSentDialog) {
            setTitle("E-posta Doğrulama")
            setMessage("Doğrulama e-postası başarıyla gönderildi.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(verificationEmailSentErrorDialog) {
            setTitle("E-posta Doğrulama Hatası")
            setMessage("Doğrulama e-postası gönderilemedi. Tekrar denemek ister misiniz?")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Evet"
            ) { _, _ ->
                loginViewModel.sendVerificationEmail()
                loginViewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Hayır"
            ) { dialog, _ ->
                dialog.cancel()
                loginViewModel.signOut()
            }
        }

        with(forgotPasswordDialog) {
            setTitle("Şifremi Unuttum")
            setMessage("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Kapat"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(forgotPasswordSuccessDialog) {
            setTitle("Şifremi Unuttum")
            setMessage("Yeni şifreniz e-posta adresinize gönderildi.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
        with(emailVerificationReminderDialog) {
            setTitle("E-posta Doğrulama Gerekli")
            setMessage("E-posta adresinizi doğrulamadınız. Lütfen e-postanızı kontrol edin ve doğrulama işlemini tamamlayın. Eğer doğrulama e-postası ulaşmadıysa, tekrar gönderebiliriz.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tekrar Gönder"
            ) { _, _ ->
                loginViewModel.sendVerificationEmail()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Kapat"
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
                    loginViewModel.signInWithGoogle(token)
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
