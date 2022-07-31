package com.example.tracingapp.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.example.tracingapp.data.model.User
import com.example.tracingapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern

class UserViewModel(private val repository: UserRepository): ViewModel(), Observable {
    @Bindable
    val userId = MutableLiveData<String>()
    @Bindable
    val userVerification = MutableLiveData<String>()
    @Bindable
    val userPassword = MutableLiveData<String>()
    @Bindable
    val userVaccination = MutableLiveData<String>()
    @Bindable
    val inputFullname = MutableLiveData<String>()
    @Bindable
    val inputLocation = MutableLiveData<String>()
    @Bindable
    val inputPhone = MutableLiveData<String>()
    @Bindable
    val inputIc = MutableLiveData<String>()
    @Bindable
    val inputPassword = MutableLiveData<String>()
    @Bindable
    val inputConfirmPass = MutableLiveData<String>()

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean>
        get() = _navigateToMain

    private val _errorToast = MutableLiveData<Boolean>()
    val errorToast: LiveData<Boolean>
        get() = _errorToast

    private val _errorIc = MutableLiveData<Boolean>()
    val errorIc: LiveData<Boolean>
        get() = _errorIc

    private val _errorPhone = MutableLiveData<Boolean>()
    val errorPhone: LiveData<Boolean>
        get() = _errorPhone

    private val _errorPass = MutableLiveData<Boolean>()
    val errorPass: LiveData<Boolean>
        get() = _errorPass

    private val _errorNotMatch = MutableLiveData<Boolean>()
    val errorNotMatch: LiveData<Boolean>
        get() = _errorNotMatch

    val loginUserDetails: LiveData<User> = repository.loginUserDetails.asLiveData()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // ic conditions
    fun validateIc(input: String): Boolean {
        val PATTERN: Pattern = Pattern.compile("^" + ".{12}" + "$")
        return PATTERN.matcher(input).matches()
    }

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

    fun updateUser() {
        if(inputFullname.value ==null ||
            inputIc.value == null ||
            inputLocation.value == null ||
            inputPhone.value == null) { // empty inputs
            _errorToast.value = true
        } else {
            if(!validateIc(inputIc.value.toString())) { // validate ic
                _errorIc.value = true
            } else if(!validatePhone(inputPhone.value.toString())) { // validate phone
                _errorPhone.value = true
            } else if(inputPassword.value != null && !validatePass(inputPassword.value.toString())) { // validate password (if have)
                _errorPass.value = true
                if(inputPassword.value.toString() != inputConfirmPass.value.toString()) { // check password matches
                    _errorNotMatch.value = true
                }
            } else {
                uiScope.launch {
                    val fullname: String = inputFullname.value!!
                    val phone: String = inputPhone.value!!
                    val location: String = inputLocation.value!!
                    val ic: String = inputIc.value!!
                    val id: Int = userId.value!!.toInt()
                    val verification: Boolean = userVerification.value.toBoolean()!!
                    val vaccine: String = userVaccination.value!!
                    var password: String?

                    if(inputPassword.value != null ) { // new password set
                        password = hashPass(inputPassword.value!!)
                    } else { // no new password set
                        password = userPassword.value!!
                    }
                    val user = User(id, fullname, password, phone, ic, location, vaccine, verification, true)
                    editUser(user)
                    _navigateToMain.value = true
                }
            }
        }
    }

    fun logout() { repository.logoutUser() }
    fun doneNavigate() { _navigateToMain.value = false }
    fun doneToast() { _errorToast.value = false }
    fun doneErrorIc() { _errorIc.value = false }
    fun doneErrorPhone() { _errorPhone.value = false }
    fun doneErrorPass() { _errorPass.value = false }
    fun doneErrorMatchPass() { _errorNotMatch.value = false }

    private fun editUser(user: User): Job =
        viewModelScope.launch(Dispatchers.IO) { repository.updateUser(user) }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
}

class UserViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel not found!")
    }
}