package com.example.rentalmanagement.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.rentalmanagement.R
import com.example.rentalmanagement.databinding.ToastConditionBinding

class ToastUtils {
    companion object {
        fun createToast(context: Context, title: String, message: String, condition: String) {
            // Inflate the custom layout
            val inflater = LayoutInflater.from(context)
            val binding = ToastConditionBinding.inflate(inflater, null, false)

            // Find and customize the views
            when (condition) {
                KeyTagUtils.SUCCESS -> {
                    binding.toastConditionIcon.setImageResource(R.drawable.success)
                }

                KeyTagUtils.WARNING -> {
                    binding.toastConditionIcon.setImageResource(R.drawable.warning)
                }

                KeyTagUtils.ERROR -> {
                    binding.toastConditionIcon.setImageResource(R.drawable.error)
                }

                KeyTagUtils.LOGO -> {
                    binding.toastConditionIcon.setImageResource(R.drawable.house)
                }
            }
            if (title.isEmpty()) {
                binding.toastConditionTitle.visibility = View.GONE
            } else {
                binding.toastConditionTitle.text = title
            }

            if (message.isEmpty()) {
                binding.toastConditionDescription.visibility = View.GONE
            } else {
                binding.toastConditionDescription.text = message
            }

            val toast = Toast(context)
            toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 30)
            toast.duration = Toast.LENGTH_LONG
            toast.view = binding.root

            // Show the Toast
            toast.show()
        }

        fun createToast(context: Context, message: String) {
            createToast(context, message, "", KeyTagUtils.LOGO)
        }
    }
}