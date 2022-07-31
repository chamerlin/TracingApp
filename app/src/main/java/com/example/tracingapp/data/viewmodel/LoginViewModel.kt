package com.example.tracingapp.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tracingapp.data.model.User
import com.example.tracingapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest

class LoginViewModel(private val repository: UserRepository): ViewModel(), Observable {
    @Bindable
    val inputPhone = MutableLiveData<String>()
    @Bindable
    val inputPassword = MutableLiveData<String>()

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    private val _errorToast = MutableLiveData<Boolean>()
    val errorToast: LiveData<Boolean>
        get() = _errorToast

    private val _errorPhone = MutableLiveData<Boolean>()
    val errorPhone: LiveData<Boolean>
        get() = _errorPhone

    private val _errorPassword = MutableLiveData<Boolean>()
    val errorPassword: LiveData<Boolean>
        get() = _errorPassword

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun hashPass(input: String): String {
        val passwordToHash: String = input
        var hashedPassword: String? = null

        val hash = MessageDigest.getInstance("MD5")
        hashedPassword = BigInteger(1, hash.digest(passwordToHash.toByteArray())).toString(16).padStart(32, '0')

        return hashedPassword
    }

    fun loginUser() {
        if(inputPhone.value == null || inputPassword.value == null) { // empty inputs
            _errorToast.value = true
        } else {
            uiScope.launch {
                val user: User = repository.loginUser(inputPhone.value.toString(), hashPass(inputPassword.value.toString()))
                if(user != null) { // user exists
                    if(user.password == hashPass(inputPassword.value.toString())) { // correct password
                        _navigateToHome.value = true
                        inputPhone.value = ""
                        inputPassword.value = ""
                    } else { // wrong password
                        _errorPassword.value = true
                    }
                } else { // user doesn't exists
                    _errorPhone.value = true
                }
            }
        }
    }

    fun logout() { repository.logoutUser() }
    fun doneNavigateToHome() { _navigateToHome.value = false }
    fun doneToast() { _errorToast.value = false }
    fun doneErrorPhone() { _errorPhone.value = false }
    fun doneErrorPassword() { _errorPassword.value = false }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
}

class LoginViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel not found!")
    }
}