package com.example.tracingapp.ui.history

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tracingapp.BaseApplication
import com.example.tracingapp.ui.menu.DetailsActivity
import com.example.tracingapp.ui.menu.faq.FaqActivity
import com.example.tracingapp.R
import com.example.tracingapp.data.viewmodel.HistoryViewModel
import com.example.tracingapp.data.viewmodel.HistoryViewModelFactory
import com.example.tracingapp.data.viewmodel.UserViewModel
import com.example.tracingapp.data.viewmodel.UserViewModelFactory
import com.example.tracingapp.databinding.ActivityHistoryBinding
import com.example.tracingapp.ui.WelcomeActivity

class HistoryActivity : AppCompatActivity() {
    private var adapter = HistoryAdapter()
    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as BaseApplication).historyRepository)
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as BaseApplication).userRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        actionBar?.setTitle(Html.fromHtml("<big><big>History</big></big>"))

        userViewModel.loginUserDetails.observe(this) {
            historyViewModel.ownHistories(it.userId).observe(this) { histories ->
                histories.let { adapter.submitList(it) }
            }
        }

        binding.rvHistory?.adapter = adapter
        binding.rvHistory?.adapter?.notifyDataSetChanged()
        binding.rvHistory?.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.nav_details -> {
                val intent: Intent = Intent(this, DetailsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
                true
            }
            R.id.nav_faq -> {
                val intent: Intent = Intent(this@HistoryActivity, FaqActivity::class.java)
                startActivity(intent)
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
                val intent: Intent = Intent(this@HistoryActivity, WelcomeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}