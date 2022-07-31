package com.example.tracingapp.ui.menu

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.tracingapp.BaseApplication
import com.example.tracingapp.MainActivity
import com.example.tracingapp.R
import com.example.tracingapp.data.viewmodel.UserViewModel
import com.example.tracingapp.data.viewmodel.UserViewModelFactory
import com.example.tracingapp.databinding.ActivityDetailsBinding
import com.example.tracingapp.ui.WelcomeActivity
import com.example.tracingapp.ui.history.HistoryActivity
import com.example.tracingapp.ui.menu.faq.FaqActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as BaseApplication).userRepository)
    }
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        actionBar?.setTitle(Html.fromHtml("<big><big>My Details</big></big>"))

        val location_type = resources.getStringArray(R.array.location_type)
        val arrayAdapter = ArrayAdapter(this, R.layout.items_dropdown, location_type)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        binding.tvContact.text = "Contact (+60)"

        userViewModel.loginUserDetails.observe(this) {
            binding.etUsername.setText(it.fullname)
            binding.etUserContact.setText(it.phone)
            binding.etUserIc.setText(it.ic)
            binding.autoCompleteTextView.setHint(it.location)
            binding.dataUserId.setText(it.userId.toString())
            binding.dataUserPass.setText(it.password)
            binding.dataUserVerification.setText(it.isVerified.toString())
            binding.dataUserVaccine.setText(it.vaccine)

            if(it.isVerified) {
                binding.etUsername.isEnabled = false
                binding.etUserIc.isEnabled = false
            }
        }

        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        userViewModel.errorToast.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Please fill up all the required fields and choose a location.", Toast.LENGTH_SHORT).show()
                userViewModel.doneToast()
            }
        })

        userViewModel.errorIc.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Wrong IC/Passport format! Please make sure it only contains 12 numbers, no dash(-) and try again.", Toast.LENGTH_SHORT).show()
                userViewModel.doneErrorIc()
            }
        })

        userViewModel.errorPhone.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Wrong phone number format! Please ensure that it is only 9 or 10 numbers long and try again", Toast.LENGTH_SHORT).show()
                userViewModel.doneErrorPhone()
            }
        })

        userViewModel.errorPass.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "Wrong password format! Make sure password has no white space and at least one special character", Toast.LENGTH_SHORT).show()
                userViewModel.doneErrorPass()
            }
        })

        userViewModel.errorNotMatch.observe(this, Observer { hasError ->
            if(hasError) {
                Toast.makeText(this, "New password and new confirm password do not match! Please try again.", Toast.LENGTH_SHORT).show()
                userViewModel.doneErrorMatchPass()
            }
        })

        userViewModel.navigateToMain.observe(this, Observer { isFinished ->
            if(isFinished) {
                saveDetails()
                userViewModel.doneNavigate()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.nav_faq -> {
                val intent: Intent = Intent(this, FaqActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
                true
            }
            R.id.nav_history -> {
                val intent: Intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
                true
            }
            R.id.nav_helpdesk -> {
                val uri: String = "https://mysejahtera.malaysia.gov.my/help_en/"

                val i: Intent = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(uri))
                startActivity(i)
                true
            }
            R.id.nav_logout -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    userViewModel.logout()
                }
                Toast.makeText(this@DetailsActivity, "Successfully Logout!", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun saveDetails() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        Toast.makeText(this, "Personal details has been successfully updated!", Toast.LENGTH_LONG).show()
    }

    fun closeActivity(view: View) {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
    }
}