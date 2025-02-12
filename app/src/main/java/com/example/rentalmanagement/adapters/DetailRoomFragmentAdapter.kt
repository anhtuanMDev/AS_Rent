package com.example.rentalmanagement.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rentalmanagement.fragments.BindDetailPersonFragment
import com.example.rentalmanagement.models.EntityPeople

class DetailRoomFragmentAdapter(
    fragmentActivity: FragmentActivity,
    var fragments: List<EntityPeople> = emptyList()
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        val detail = fragments[position]
        return BindDetailPersonFragment.newInstance(
            registrationDate = detail.validateDate,
            birthday = detail.birth,
            phoneNumber = detail.phoneNumber,
            deposit = "${detail.deposit}",
            rentDate = detail.startRentDate,
            permanentAddress = detail.permanentAddress,
            roleInHouse = detail.roleInHouse,
            name = detail.name,
            gender = detail.gender,
            validateDate = detail.validateDate,
            email = detail.email,
            identifyID = detail.identifyID,
            comingUpPayDate = detail.comingUpPayDate!!
        )
    }

    fun updateData(newFragments: List<EntityPeople>) {
        fragments = emptyList()
        fragments = newFragments
        notifyDataSetChanged()
    }
}
