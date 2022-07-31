package com.example.tracingapp.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.tracingapp.BaseApplication
import com.example.tracingapp.R
import com.example.tracingapp.data.model.History
import com.example.tracingapp.data.viewmodel.HistoryViewModel
import com.example.tracingapp.data.viewmodel.HistoryViewModelFactory
import com.example.tracingapp.data.viewmodel.UserViewModel
import com.example.tracingapp.data.viewmodel.UserViewModelFactory
import com.example.tracingapp.databinding.FragmentScannerBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class ScannerFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as BaseApplication).userRepository)
    }
    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((activity?.application as BaseApplication).historyRepository)
    }
    private lateinit var binding: FragmentScannerBinding
    var userId: Int? = null
    var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.setTitle(Html.fromHtml("<big><big>Check-in</big></big>"))

        binding = FragmentScannerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.loginUserDetails.observe(viewLifecycleOwner) {
            binding.tvUsername.text = it.fullname
            binding.tvIc.text = it.ic
            userId = it.userId
            userName = it.fullname

            if(it.isVerified) {
                binding.tvVerify.text = "Verified"
                binding.tvVerify.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0)
            } else {
                binding.tvVerify.text = "Not Verified"
                binding.tvVerify.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_not_verified, 0)
            }
        }

        binding.btnCheckIn.setOnClickListener {
            val options = ScanOptions()

            options.apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("Scan")
                setCameraId(0)
                setBeepEnabled(true)
                setBarcodeImageEnabled(true)
                setOrientationLocked(true)
            }
            zXingQRCodeScanLauncher.launch(options)
        }
    }

    @SuppressLint("NewApi")
    private val zXingQRCodeScanLauncher = registerForActivityResult(ScanContract()) {
        result ->
        if(result.contents == null) {
            checkInFail()
        } else {
            val location = result.contents
            // not Malaysia time
//            val date = SimpleDateFormat("MMM d yyyy, EEE")
//            val time = SimpleDateFormat("hh:mm:ss aa")
//            val currentTime = time.format(Date())
//            val currentDate = date.format(Date())

            val time = LocalTime.now().toString()
            val date = LocalDate.now().toString()

            checkInSuccess(location, date, time, userName)
            val history = History(0, location, date, time, userId!!)
            historyViewModel.checkInUser(history)
        }
    }

    fun checkInSuccess(location: String, date: String, time: String, username: String) {
        val view = View.inflate(requireContext(), R.layout.scan_success, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(view)

        dialog.findViewById<TextView>(R.id.tv_user_name).text = username
        dialog.findViewById<TextView>(R.id.tv_place_name).text = location
        dialog.findViewById<TextView>(R.id.tv_checkin_date).text = date
        dialog.findViewById<TextView>(R.id.tv_checkin_time).text = time
        dialog.show()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun checkInFail() {
        val view = View.inflate(requireContext(), R.layout.scan_success, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(view)

        // unable to use findViewById here
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setView(view)

//        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}