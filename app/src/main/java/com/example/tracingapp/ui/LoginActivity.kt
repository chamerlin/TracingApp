package com.example.tracingapp.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.tracingapp.BaseApplication
import com.example.tracingapp.MainActivity
import com.example.tracingapp.R
import com.example.tracingapp.data.viewmodel.LoginViewModel
import com.example.tracingapp.data.viewmodel.LoginViewModelFactory
import com.example.tracingapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val loginviewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as BaseApplication).userRepository)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.on_primary_dark)))
        actionBar?.setTitle(Html.fromHtml("<big><big>Login</big></big>"))

        binding.loginViewModel = loginviewModel
        binding.lifecycleOwner = this

        lifecycleScope.launch(Dispatchers.IO) {
            loginviewModel.logout()
        }

        loginviewModel.errorToast.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()
                loginviewModel.doneToast()
            }
        })

        loginviewModel.errorPhone.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "User not found. Please register a new account!", Toast.LENGTH_SHORT).show()
                loginviewModel.doneErrorPhone()
            }
        })

        loginviewModel.errorPassword.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Wrong password! Please try again", Toast.LENGTH_SHORT).show()
                loginviewModel.doneErrorPassword()
            }
        })

        loginviewModel.navigateToHome.observe(this, Observer { hasFinished ->
            if(hasFinished) {
                Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
                Main()
                loginviewModel.doneNavigateToHome()
            }
        })
    }

    fun ForgotPass(view: View) {
        val intent = Intent(this, ForgotPassActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }

    fun Register(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }

    fun Main() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
        finish()
    }
}