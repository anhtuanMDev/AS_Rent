package com.example.rentalmanagement.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.AddressViewModel
import com.example.rentalmanagement.databinding.FragmentInputCountryBinding

class InputCountry_Fragment : Fragment() {
    private val viewModel: AddressViewModel by activityViewModels()
    private lateinit var edt: AutoCompleteTextView
    private lateinit var bind: FragmentInputCountryBinding

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: InputCountry_Fragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: InputCountry_Fragment")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: InputCountry_Fragment")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: InputCountry_Fragment")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: InputCountry_Fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentInputCountryBinding.inflate(inflater, container, false)
        edt = bind.edtCountry

        // Set up the listener for when the country text field loses focus (blur)

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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        edt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateCountryInViewModel()
                findNavController().navigate(R.id.inputCity_Fragment)
            }
        }
    }

    // Update the ViewModel with the new country value
    private fun updateCountryInViewModel() {
        val country = edt.text.toString()
        if (country.isNotBlank()) {
            viewModel.updateAddressField("country", country)
        }
    }

    private fun updateCountryInViewModel(text: String) {
        viewModel.updateAddressField("country", text)
    }

    // Helper function to hide the keyboard
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(edt.windowToken, 0)
    }

    companion object {
        val TAG = this@Companion::class.simpleName
    }
}
