package com.example.logingoogle.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.logingoogle.model.AuthRepository
import com.example.logingoogle.model.EventWrapper
import com.example.logingoogle.model.User
import com.google.firebase.auth.AuthCredential

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {
    enum class SignInType(val type: String) {
        Google("Google")
    }

    val authButtonLiveData = MutableLiveData<EventWrapper<SignInType>>()
    var authenticateUserLiveData = MutableLiveData<User>()

    fun handleGoogleSignInClick() {
        authButtonLiveData.value = EventWrapper(SignInType.Google)
    }

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        authenticateUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    class AuthViewModelFactory(private val authRepository: AuthRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}