package com.example.tracingapp.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.tracingapp.BaseApplication
import com.example.tracingapp.MainActivity
import com.example.tracingapp.R
import com.example.tracingapp.data.viewmodel.RegisterViewModel
import com.example.tracingapp.data.viewmodel.RegisterViewModelFactory
import com.example.tracingapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory((application as BaseApplication).userRepository)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.on_primary_dark)))
        actionBar?.setTitle(Html.fromHtml("<big><big>Register</big></big>"))

        val dropdown = binding.autoCompleteTextView

        val location_type = resources.getStringArray(R.array.location_type)
        val arrayAdapter = ArrayAdapter(this, R.layout.items_dropdown, location_type)
        dropdown.setAdapter(arrayAdapter)

        binding.registerViewModel = registerViewModel
        binding.lifecycleOwner = this

        registerViewModel.errorToast.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()
                registerViewModel.doneToast()
            }
        })

        registerViewModel.errorFormatPhone.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Wrong phone format! Make sure there are at least 9 numbers", Toast.LENGTH_SHORT).show()
                registerViewModel.doneFormatPhone()
            }
        })

        registerViewModel.errorFormatPass.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Wrong password format! Make sure password has no white space, at least one special character and a capital letter", Toast.LENGTH_SHORT).show()
                registerViewModel.doneFormatPass()
            }
        })

        registerViewModel.errorPhone.observe(this, Observer { isFound ->
            if(isFound) {
                Toast.makeText(this, "User already exists! Please login.", Toast.LENGTH_SHORT).show()
                registerViewModel.doneErrorPhone()
            }
        })

        registerViewModel.errorMatchPass.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Password and Confirm Password does not match! Please try again.", Toast.LENGTH_SHORT).show()
                registerViewModel.doneErrorMatchPass()
            }
        })

        registerViewModel.navigateToLogin.observe(this, Observer { hasFinished ->
            if(hasFinished) {
                Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_SHORT).show()
                LoginToMain()
                registerViewModel.doneToLogin()
            }
        })
    }

    fun Login(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }

    fun LoginToMain() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }
}