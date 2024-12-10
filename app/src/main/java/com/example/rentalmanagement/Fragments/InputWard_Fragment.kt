package com.example.rentalmanagement.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.AddressViewModel
import com.example.rentalmanagement.databinding.FragmentInputWardBinding

class InputWard_Fragment : Fragment() {
    private lateinit var edt: EditText
    private lateinit var rcv: RecyclerView
    private lateinit var bind: FragmentInputWardBinding

    private val viewModel: AddressViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind =  FragmentInputWardBinding.inflate(layoutInflater, container, false)
        edt = bind.edtWard
        rcv = bind.rcvWard

        edt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateCountryInViewModel()
            }
        }

        // Listen for the "Done" action on the keyboard and blur the field
        edt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                updateCountryInViewModel()
                edt.clearFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        return bind.root
    }

    private fun updateCountryInViewModel() {
        val country = edt.text.toString()
        if (country.isNotBlank()) {
            viewModel.updateAddressField("country", country)
            findNavController().navigate(R.id.inputOtherAddressInfo_Fragment)
        }
    }

    private fun updateCountryInViewModel(text: String) {
        viewModel.updateAddressField("country", text)
        findNavController().navigate(R.id.inputOtherAddressInfo_Fragment)
    }

    // Helper function to hide the keyboard
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(edt.windowToken, 0)
    }

}