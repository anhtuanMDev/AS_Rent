package com.example.rentalmanagement.Fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rentalmanagement.R
import com.example.rentalmanagement.Utils.NumberUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BindDetailPersonFragment : Fragment(R.layout.detail_person) {

    private var registrationDate: String? = null
    private var role: String? = null
    private var birthday: String? = null
    private var phoneNumber: String? = null
    private var deposit: String? = null
    private var validateDateText: String? = null
    private var comingUpPayDate: String? = null
    private var permanentAddressText: String? = null
    private var email: String? = null
    private var name: String? = null
    private var identifyIDText: String? = null
    private var gender: String? = null
    private var comingUpPay: String? = null
    private var rentDate: String? = null

    companion object {
        fun newInstance(
            name: String,
            email: String,
            gender: String,
            deposit: String,
            birthday: String,
            rentDate: String,
            identifyID: String,
            roleInHouse: String,
            phoneNumber: String,
            validateDate: String,
            permanentAddress: String,
            registrationDate: String,
            comingUpPayDate: String
        ): BindDetailPersonFragment {
            val fragment = BindDetailPersonFragment()
            val args = Bundle().apply {
                putString("name", name)
                putString("email", email)
                putString("gender", gender)
                putString("deposit", deposit)
                putString("rentDate", rentDate)
                putString("birthday", birthday)
                putString("identifyID", identifyID)
                putString("roleInHouse", roleInHouse)
                putString("phoneNumber", phoneNumber)
                putString("validateDate", validateDate)
                putString("registrationDate", registrationDate)
                putString("permanentAddress", permanentAddress)
                putString("comingUpPayDate", comingUpPayDate)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            registrationDate = it.getString("registrationDate")
            role = it.getString("role")
            birthday = it.getString("birthday")
            phoneNumber = it.getString("phoneNumber")
            deposit = it.getString("deposit")
            rentDate = it.getString("rentDate")
            name = it.getString("name")
            gender = it.getString("gender")
            role = it.getString("roleInHouse")
            email = it.getString("email")
            identifyIDText = it.getString("identifyID")
            permanentAddressText = it.getString("permanentAddress")
            validateDateText = it.getString("validateDate")
            comingUpPayDate = it.getString("comingUpPayDate")
            comingUpPay = it.getString("comingUpPay")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        val roleView: TextView = view.findViewById(R.id.detail_person_role_value)
        val nameView: TextView = view.findViewById(R.id.detail_person_name_value)
        val avatar: ImageView = view.findViewById(R.id.detail_person_avatar)
        val birthdayView: TextView = view.findViewById(R.id.detail_person_birthday_value)
        val emailLayout: LinearLayout = view.findViewById(R.id.detail_person_email_layout)
        val emailView: TextView = view.findViewById(R.id.detail_person_email_value)
        val phoneLayout: LinearLayout = view.findViewById(R.id.detail_person_phone_layout)
        val phoneNumberView: TextView = view.findViewById(R.id.detail_person_phoneNumber_value)
        val depositLayout: LinearLayout = view.findViewById(R.id.detail_person_deposit_layout)
        val depositView: TextView = view.findViewById(R.id.detail_person_deposit_value)
        val payRentDateView: TextView = view.findViewById(R.id.detail_person_rent_value)
        val validateStatus: TextView = view.findViewById(R.id.detail_person_validate_status)
        val validateDate: TextView = view.findViewById(R.id.detail_person_validate_date)
        val startRentView: TextView = view.findViewById(R.id.detail_person_start_rent_value)
        val permanentAddress: TextView = view.findViewById(R.id.detail_person_permanent_resident)
        val identification: TextView = view.findViewById(R.id.detail_person_identification_value)

        // Calculate age
        val age = birthday?.let { calculateAge(it, "dd/MM/yyyy") }

        if (age != null && age > 14) {
            avatar.setImageResource(R.drawable.adult)
        } else {
            avatar.setImageResource(R.drawable.baby)
        }

        val numberUtils = NumberUtils()
        val depositString = numberUtils.convertCurrency(deposit?.toInt() ?: 0)

        val birthString =
            birthday?.let {
                StringBuilder(it)
                    .append(" ($age)").toString()
            }

        val permanentAddressString = context?.getString(R.string.form_permanent_resident)?.let {
            StringBuilder(it)
                .append(" ", permanentAddressText).toString()
        }
        val validateStatusString = context?.getString(R.string.detail_people_validate)?.let {
            val status = if (validateDateText.isNullOrEmpty()) "Chưa xác minh" else "Đã xác minh"
            StringBuilder(it)
                .append(" ", status).toString()
        }

        if (role != context?.getString(R.string.detail_people_role_main_house)) {
            depositLayout.visibility = View.GONE
        }

        // Set data
        roleView.text = role
        birthdayView.text = birthString
        nameView.text = name
        startRentView.text = rentDate
        validateStatus.text = validateStatusString
        validateDate.text = validateDateText
        permanentAddress.text = permanentAddressString
        identification.text = identifyIDText
        payRentDateView.text = comingUpPayDate

        //Hide if don't have value
        depositView.text = depositString

        if (email.isNullOrEmpty()) {
            emailLayout.visibility = View.GONE
        } else {
            emailView.text = email
        }

        if (phoneNumber.isNullOrEmpty()) {
            phoneLayout.visibility = View.GONE
        } else {
            phoneNumberView.text = phoneNumber
        }
    }

    fun calculateAge(birthDateString: String, dateFormat: String): Int {
        // Define the date formatter
        val dateFormatParser = SimpleDateFormat(dateFormat, Locale.getDefault())
        dateFormatParser.isLenient = false
        // Parse the input date string into a Date object
        val birthDate = dateFormatParser.parse(birthDateString) ?: return -1
        // Get the current date
        val today = Calendar.getInstance()
        // Set the birth date in a Calendar object
        val birthDay = Calendar.getInstance()
        birthDay.time = birthDate
        // Calculate the age
        var age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)
        // Adjust the age if the birth date hasn't occurred yet this year
        if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}
