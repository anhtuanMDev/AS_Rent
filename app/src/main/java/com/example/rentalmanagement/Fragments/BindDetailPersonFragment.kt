package com.example.rentalmanagement.Fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rentalmanagement.R

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
    private var identifyID: String? = null
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
            registrationDate: String
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
            email = it.getString("email")
            identifyID = it.getString("identifyID")
            permanentAddressText = it.getString("permanentAddress")
            validateDateText = it.getString("validateDate")
            comingUpPayDate = it.getString("comingUpPayDate")
            comingUpPay = it.getString("comingUpPay")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        val roleView: TextView = view.findViewById(R.id.detail_person_role)
        val birthdayView: TextView = view.findViewById(R.id.detail_person_birthday)
        val phoneNumberView: TextView = view.findViewById(R.id.detail_person_phoneNumber)
        val depositView: TextView = view.findViewById(R.id.detail_person_deposit)
        val rentDateView: TextView = view.findViewById(R.id.detail_person_rent)
        val validateStatus: TextView = view.findViewById(R.id.detail_person_validate_status)
        val validateDate: TextView = view.findViewById(R.id.detail_person_validate_date)
        val permanentAddress: TextView = view.findViewById(R.id.detail_person_permanent_resident)
        val identification: TextView = view.findViewById(R.id.detail_person_identification)

        val nameString = if (role == context?.getString(R.string.detail_people_role_main_house)) {
            context?.getString(R.string.detail_people_role_main_house)?.let {
                StringBuilder(it)
                    .append(" ", name)
                    .toString()
            }
        } else {
            context?.getString(R.string.detail_people_role_family)?.let {
                StringBuilder(it)
                    .append(" ", name)
                    .toString()
            }
        }

        val birthString = context?.getString(R.string.detail_people_birth)?.let {
            StringBuilder(it)
                .append(" ", birthday).toString()
        }
        val phoneString = context?.getString(R.string.detail_people_phone)?.let {
            StringBuilder(it)
                .append(" ", phoneNumber).toString()
        }
        val depositString = context?.getString(R.string.detail_people_deposit)?.let {
            StringBuilder(it)
                .append(" ", deposit).toString()
        }
        val rentDateString = context?.getString(R.string.detail_people_pay_date)?.let {
            StringBuilder(it)
                .append(" ", rentDate).toString()
        }
        val validateDateString = context?.getString(R.string.detail_people_validate_date)?.let {
            StringBuilder(it)
                .append(" ", validateDateText).toString()
        }
        val permanentAddressString = context?.getString(R.string.detail_people_permanent_resident)?.let {
            StringBuilder(it)
                .append(" ", permanentAddressText).toString()
        }
        val identificationString = context?.getString(R.string.detail_people_id)?.let {
            StringBuilder(it)
                .append(" ", identifyID).toString()
        }
        val validateStatusString = context?.getString(R.string.detail_people_validate)?.let {
            val status = if (validateDateText.isNullOrEmpty()) "Chưa xác minh" else "Đã xác minh"
            StringBuilder(it)
                .append(" ", status).toString()
        }

        // Set data
        roleView.text = nameString
        birthdayView.text = birthString
        phoneNumberView.text = phoneString
        depositView.text = depositString
        rentDateView.text = rentDateString
        validateStatus.text = validateStatusString
        validateDate.text = validateDateString
        permanentAddress.text = permanentAddressString
        identification.text = identificationString
    }
}
