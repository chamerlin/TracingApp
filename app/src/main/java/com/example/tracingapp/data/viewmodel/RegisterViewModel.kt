package com.example.tracingapp.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.example.tracingapp.data.model.User
import com.example.tracingapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern

class RegisterViewModel(private val repository: UserRepository): ViewModel(), Observable {
    @Bindable
    val inputFullname = MutableLiveData<String>()
    @Bindable
    val inputPhone = MutableLiveData<String>()
    @Bindable
    val inputLocation = MutableLiveData<String>()
    @Bindable
    val inputPassword = MutableLiveData<String>()
    @Bindable
    val inputConfirmPass = MutableLiveData<String>()

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean>
        get() = _navigateToLogin

    private val _errorToast = MutableLiveData<Boolean>()
    val errorToast: LiveData<Boolean>
        get() = _errorToast

    private val _errorFormatPass = MutableLiveData<Boolean>()
    val errorFormatPass: LiveData<Boolean>
        get() = _errorFormatPass

    private val _errorFormatPhone = MutableLiveData<Boolean>()
    val errorFormatPhone: LiveData<Boolean>
        get() = _errorFormatPhone

    private val _errorPhone = MutableLiveData<Boolean>()
    val errorPhone: LiveData<Boolean>
        get() = _errorPhone

    private val _errorMatchPass = MutableLiveData<Boolean>()
    val errorMatchPass: LiveData<Boolean>
        get() = _errorMatchPass

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // phone conditions
    fun validatePhone(input: String): Boolean {
        val PATTERN: Pattern = Pattern.compile("^" + ".{9,}" + "$")
        return PATTERN.matcher(input).matches()
    }

    // password conditions
    fun validatePass(input: String): Boolean {
        val PATTERN: Pattern = Pattern.compile("^" + "(?=.*[@#$%^&+=])" +  "(?=\\S+$)" + "(?=.*[A-Z])" + ".{6,}" + "$")
        return PATTERN.matcher(input).matches()
    }

    fun hashPass(input: String): String {
        val passwordToHash: String = input
        var hashedPassword: String? = null

        val hash = MessageDigest.getInstance("MD5")
        hashedPassword = BigInteger(1, hash.digest(passwordToHash.toByteArray())).toString(16).padStart(32, '0')

        return hashedPassword
    }

    fun registerUser() {
        if(inputFullname.value == null||
            inputPhone.value == null ||
            inputLocation.value == null ||
            inputPassword.value == null ||
            inputConfirmPass.value == null
        ) { // empty inputs
            _errorToast.value = true
        } else if (!validatePhone(inputPhone.value.toString())) { // validate phone
            _errorFormatPhone.value = true
        } else if(!validatePass(inputPassword.value.toString())) { // validate password
            _errorFormatPass.value = true
        } else if(inputPassword.value != inputConfirmPass.value) { // check password matches
            _errorMatchPass.value = true
        } else {
            uiScope.launch {
                val userInfo = repository.findUser(inputPhone.value.toString())
                if(userInfo == null) { // user not found
                    val fullname: String = inputFullname.value!!
                    val phone: String = inputPhone.value!!
                    val location: String = inputLocation.value!!
                    val vaccine: String = "Not found!"
                    val password: String = inputPassword.value!!
                    val ic: String = "-"

                    val user = User(0, fullname, hashPass(password), phone, ic, location, vaccine, false, false)
                    registerUsers(user)
                    _errorPhone.value = false
                    _navigateToLogin.value = true
                    inputFullname.value = ""
                    inputPhone.value = ""
                    inputLocation.value = ""
                    inputPassword.value = ""
                    inputConfirmPass.value = ""
                } else { // user found
                    _errorPhone.value = true
                }
            }
        }
    }

    fun doneToLogin() { _navigateToLogin.value = false }
    fun doneToast() { _errorToast.value = false }
    fun doneFormatPass() { _errorFormatPass.value = false }
    fun doneFormatPhone() { _errorFormatPhone.value = false }
    fun doneErrorPhone() { _errorPhone.value = false }
    fun doneErrorMatchPass() { _errorMatchPass.value = false }

    private fun registerUsers(user: User): Job =
        viewModelScope.launch(Dispatchers.IO) { repository.registerUser(user) }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
}

class RegisterViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel not found!")
    }

}