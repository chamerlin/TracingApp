package com.example.tracingapp.ui.fragment

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.tracingapp.BaseApplication
import com.example.tracingapp.R
import com.example.tracingapp.data.viewmodel.UserViewModel
import com.example.tracingapp.data.viewmodel.UserViewModelFactory
import com.example.tracingapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as BaseApplication).userRepository)
    }
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.setTitle(Html.fromHtml("<big><big>Profile</big></big>"))

        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.loginUserDetails.observe(viewLifecycleOwner) {
            binding.tvUsername.text = it.fullname
            binding.tvUserIc.text = it.ic
            binding.tvStateLocation.text = it.location + ", Malaysia"
            binding.tvUserContact.text = "+60-" + it.phone

            if(it.isVerified) {
                binding.tvVerify.text = "Verified"
                binding.tvVerify.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0)
            } else {
                binding.tvVerify.text = "Not Verified"
                binding.tvVerify.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_not_verified, 0)
            }
        }
    }
}