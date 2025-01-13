package com.example.rentalmanagement.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rentalmanagement.Fragments.BindDetailPersonFragment
import com.example.rentalmanagement.Models.EntityPeople

class DetailRoomFragmentAdapter(
    fragmentActivity: FragmentActivity,
    private val fragments: List<EntityPeople>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        val detail = fragments[position]
        return BindDetailPersonFragment.newInstance(
            registrationDate = detail.validateDate,
            birthday = detail.birth,
            phoneNumber = detail.phoneNumber,
            deposit = "${detail.deposit}",
            rentDate = "${detail.comingUpPayDate}",
            permanentAddress = detail.permanentAddress,
            roleInHouse = detail.roleInHouse,
            name = detail.name,
            gender = detail.gender,
            validateDate = detail.validateDate,
            email = detail.email,
            identifyID = detail.identifyID,
        )
    }

}
