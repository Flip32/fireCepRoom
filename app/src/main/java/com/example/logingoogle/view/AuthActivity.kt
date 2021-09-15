package com.example.logingoogle.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.example.logingoogle.viewModel.AuthViewModel
import com.example.logingoogle.databinding.ActivityAuthBinding
import com.example.logingoogle.R
import com.example.logingoogle.model.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        authViewModel = ViewModelProvider(this, AuthViewModel.AuthViewModelFactory(AuthRepository()))
            .get(AuthViewModel::class.java)
        initRegistersAndObservers()
    }

    private fun initRegistersAndObservers() {
        binding.googleButtonSingIn.setOnClickListener{
            authViewModel.handleGoogleSignInClick()
        }

        authViewModel.authButtonLiveData.observe(this) { signInType ->
            signInType.getContentIfNotHandled()?.let { type ->
                when(type) {
                    AuthViewModel.SignInType.Google -> signInWithGoogle()
                    else -> Log.e("Sign In Type", "Not Found")
                }
            }
        }

        authViewModel.authenticateUserLiveData.observe(this) { user ->
            Log.d("user", user.email?: "Sem Email")
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.oauth_token))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        signInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val googleAccount = task.result
                if (googleAccount != null) {
                    getGoogleAuthCredential(googleAccount)
                }
            }

        }
    }

    private fun getGoogleAuthCredential(googleAccount: GoogleSignInAccount) {
        val googleTokenId = googleAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        authViewModel.signInWithGoogle(googleAuthCredential)
    }

    private fun signInWithGoogle() {
        signInResultLauncher.launch((googleSignInClient.signInIntent))
    }
}